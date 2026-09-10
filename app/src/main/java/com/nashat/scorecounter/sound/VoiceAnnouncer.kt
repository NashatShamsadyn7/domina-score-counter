package com.nashat.scorecounter.sound

import android.content.Context
import android.speech.tts.TextToSpeech
import com.nashat.scorecounter.model.AppLanguage
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Speaks score changes out loud ("15 for Nashat") using the platform
 * TextToSpeech engine. Falls back to English whenever the device has no
 * engine data for the selected app language (Kurdish is rarely available).
 */
class VoiceAnnouncer(context: Context) {

    private val ready = AtomicBoolean(false)

    @Volatile
    private var pendingLanguage: AppLanguage? = null

    @Volatile
    private var fellBackToEnglish = false

    private val tts = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            ready.set(true)
            pendingLanguage?.let { applyLanguageInternal(it) }
        }
    }

    val isReady: Boolean
        get() = ready.get()

    fun applyLanguage(language: AppLanguage) {
        pendingLanguage = language
        applyLanguageInternal(language)
    }

    private fun applyLanguageInternal(language: AppLanguage) {
        if (!ready.get()) return
        val candidates = when (language) {
            AppLanguage.KURDISH -> listOf(Locale("ku"), Locale("ckb"), Locale("en", "US"))
            AppLanguage.ARABIC -> listOf(Locale("ar"), Locale("en", "US"))
            AppLanguage.ENGLISH -> listOf(Locale("en", "US"))
        }
        fellBackToEnglish = true
        for (locale in candidates) {
            val result = tts.setLanguage(locale)
            val available = result == TextToSpeech.LANG_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
            if (available) {
                fellBackToEnglish = locale.language == "en" && language != AppLanguage.ENGLISH
                return
            }
        }
    }

    fun speak(text: String) {
        if (!ready.get()) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "domina-${System.currentTimeMillis()}")
    }

    fun announcementPhrase(value: Int, name: String, language: AppLanguage, subtract: Boolean): String {
        val effectiveLanguage = if (fellBackToEnglish) AppLanguage.ENGLISH else language
        return when (effectiveLanguage) {
            AppLanguage.ARABIC -> if (subtract) "ناقص $value لصالح $name" else "$value لصالح $name"
            AppLanguage.KURDISH -> if (subtract) "$value لە $name کەم بکەرەوە" else "$value بۆ $name"
            AppLanguage.ENGLISH -> if (subtract) "minus $value for $name" else "$value for $name"
        }
    }

    fun release() {
        ready.set(false)
        try {
            tts.stop()
            tts.shutdown()
        } catch (_: Exception) {
            // Engine already shut down; nothing to do.
        }
    }
}
