package com.nashat.scorecounter.sound

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibrationManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrateForScore(newScore: Int, delta: Int, enabled: Boolean) {
        if (!enabled || vibrator?.hasVibrator() != true) return

        val effect = when {
            delta < 0 -> oneShot(34, 110)
            newScore in setOf(100, 200, 300, 400, 500) -> wave(longArrayOf(0, 30, 22, 45), intArrayOf(0, 120, 0, 180))
            newScore % 50 == 0 && newScore in 50..550 -> wave(longArrayOf(0, 18, 14, 18), intArrayOf(0, 80, 0, 120))
            else -> oneShot(20, 70)
        }

        vibrator.vibrate(effect)
    }

    private fun oneShot(duration: Long, amplitude: Int): VibrationEffect {
        return VibrationEffect.createOneShot(duration, amplitude)
    }

    private fun wave(timings: LongArray, amplitudes: IntArray): VibrationEffect {
        return VibrationEffect.createWaveform(timings, amplitudes, -1)
    }
}
