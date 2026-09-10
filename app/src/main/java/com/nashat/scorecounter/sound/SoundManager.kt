package com.nashat.scorecounter.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.nashat.scorecounter.R
import java.util.Collections

class SoundManager(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Sounds that finished decoding. Playing an id before it is loaded is a
    // silent no-op, so we track completion to avoid broken first taps.
    private val loadedSoundIds: MutableSet<Int> = Collections.synchronizedSet(HashSet())

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
            }
        }
    }

    private fun load(context: Context, resId: Int): Int = soundPool.load(context, resId, 1)

    private val addSoundId = load(context, R.raw.add_generic)
    private val subtractSoundId = load(context, R.raw.subtract_generic)
    private val addValueSounds = mapOf(
        0 to mapOf(
            5 to load(context, R.raw.team1_add_5),
            10 to load(context, R.raw.team1_add_10),
            15 to load(context, R.raw.team1_add_15),
            20 to load(context, R.raw.team1_add_20),
            25 to load(context, R.raw.team1_add_25),
            30 to load(context, R.raw.team1_add_30)
        ),
        1 to mapOf(
            5 to load(context, R.raw.team2_add_5),
            10 to load(context, R.raw.team2_add_10),
            15 to load(context, R.raw.team2_add_15),
            20 to load(context, R.raw.team2_add_20),
            25 to load(context, R.raw.team2_add_25),
            30 to load(context, R.raw.team2_add_30)
        ),
        2 to mapOf(
            5 to load(context, R.raw.team3_add_5),
            10 to load(context, R.raw.team3_add_10),
            15 to load(context, R.raw.team3_add_15),
            20 to load(context, R.raw.team3_add_20),
            25 to load(context, R.raw.team3_add_25),
            30 to load(context, R.raw.team3_add_30)
        ),
        3 to mapOf(
            5 to load(context, R.raw.team4_add_5),
            10 to load(context, R.raw.team4_add_10),
            15 to load(context, R.raw.team4_add_15),
            20 to load(context, R.raw.team4_add_20),
            25 to load(context, R.raw.team4_add_25),
            30 to load(context, R.raw.team4_add_30)
        )
    )
    private val subtractSounds = mapOf(
        0 to load(context, R.raw.team1_subtract),
        1 to load(context, R.raw.team2_subtract),
        2 to load(context, R.raw.team3_subtract),
        3 to load(context, R.raw.team4_subtract)
    )
    private val milestoneSounds = mapOf(
        50 to load(context, R.raw.score_50),
        100 to load(context, R.raw.score_100),
        150 to load(context, R.raw.score_150),
        200 to load(context, R.raw.score_200),
        250 to load(context, R.raw.score_250),
        300 to load(context, R.raw.score_300),
        350 to load(context, R.raw.score_350),
        400 to load(context, R.raw.score_400),
        450 to load(context, R.raw.score_450),
        500 to load(context, R.raw.score_500),
        550 to load(context, R.raw.score_550)
    )

    fun playAdd(playerId: Int, value: Int, enabled: Boolean) {
        if (!enabled) return
        val soundId = addValueSounds[playerId]?.get(value) ?: addSoundId
        play(soundId, panLeft(playerId), panRight(playerId))
    }

    fun playSubtract(playerId: Int, enabled: Boolean) {
        if (!enabled) return
        val soundId = subtractSounds[playerId] ?: subtractSoundId
        play(soundId, panLeft(playerId), panRight(playerId), rate = 0.94f)
    }

    fun playMilestone(score: Int, enabled: Boolean): Boolean {
        val soundId = milestoneSounds[score] ?: return false
        if (enabled) play(soundId, 1f, 1f, priority = 2)
        return true
    }

    private fun play(soundId: Int, left: Float, right: Float, priority: Int = 1, rate: Float = 1f) {
        if (soundId !in loadedSoundIds) return
        soundPool.play(soundId, left, right, priority, 0, rate)
    }

    private fun panLeft(playerId: Int): Float = if (playerId % 2 == 0) 1f else 0.82f
    private fun panRight(playerId: Int): Float = if (playerId % 2 == 1) 1f else 0.82f

    fun release() {
        soundPool.release()
    }
}
