package com.nashat.scorecounter.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.nashat.scorecounter.R

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

    private val addSoundId = soundPool.load(context, R.raw.add_generic, 1)
    private val subtractSoundId = soundPool.load(context, R.raw.subtract_generic, 1)
    private val addValueSounds = mapOf(
        0 to mapOf(
            5 to soundPool.load(context, R.raw.team1_add_5, 1),
            10 to soundPool.load(context, R.raw.team1_add_10, 1),
            15 to soundPool.load(context, R.raw.team1_add_15, 1),
            20 to soundPool.load(context, R.raw.team1_add_20, 1),
            25 to soundPool.load(context, R.raw.team1_add_25, 1),
            30 to soundPool.load(context, R.raw.team1_add_30, 1)
        ),
        1 to mapOf(
            5 to soundPool.load(context, R.raw.team2_add_5, 1),
            10 to soundPool.load(context, R.raw.team2_add_10, 1),
            15 to soundPool.load(context, R.raw.team2_add_15, 1),
            20 to soundPool.load(context, R.raw.team2_add_20, 1),
            25 to soundPool.load(context, R.raw.team2_add_25, 1),
            30 to soundPool.load(context, R.raw.team2_add_30, 1)
        ),
        2 to mapOf(
            5 to soundPool.load(context, R.raw.team3_add_5, 1),
            10 to soundPool.load(context, R.raw.team3_add_10, 1),
            15 to soundPool.load(context, R.raw.team3_add_15, 1),
            20 to soundPool.load(context, R.raw.team3_add_20, 1),
            25 to soundPool.load(context, R.raw.team3_add_25, 1),
            30 to soundPool.load(context, R.raw.team3_add_30, 1)
        ),
        3 to mapOf(
            5 to soundPool.load(context, R.raw.team4_add_5, 1),
            10 to soundPool.load(context, R.raw.team4_add_10, 1),
            15 to soundPool.load(context, R.raw.team4_add_15, 1),
            20 to soundPool.load(context, R.raw.team4_add_20, 1),
            25 to soundPool.load(context, R.raw.team4_add_25, 1),
            30 to soundPool.load(context, R.raw.team4_add_30, 1)
        )
    )
    private val subtractSounds = mapOf(
        0 to soundPool.load(context, R.raw.team1_subtract, 1),
        1 to soundPool.load(context, R.raw.team2_subtract, 1),
        2 to soundPool.load(context, R.raw.team3_subtract, 1),
        3 to soundPool.load(context, R.raw.team4_subtract, 1)
    )
    private val milestoneSounds = mapOf(
        50 to soundPool.load(context, R.raw.score_50, 1),
        100 to soundPool.load(context, R.raw.score_100, 1),
        150 to soundPool.load(context, R.raw.score_150, 1),
        200 to soundPool.load(context, R.raw.score_200, 1),
        250 to soundPool.load(context, R.raw.score_250, 1),
        300 to soundPool.load(context, R.raw.score_300, 1),
        350 to soundPool.load(context, R.raw.score_350, 1),
        400 to soundPool.load(context, R.raw.score_400, 1),
        450 to soundPool.load(context, R.raw.score_450, 1),
        500 to soundPool.load(context, R.raw.score_500, 1),
        550 to soundPool.load(context, R.raw.score_550, 1)
    )

    fun playAdd(playerId: Int, value: Int, enabled: Boolean) {
        if (!enabled) return
        val soundId = addValueSounds[playerId]?.get(value) ?: addSoundId
        val left = if (playerId % 2 == 0) 1f else 0.82f
        val right = if (playerId % 2 == 1) 1f else 0.82f
        soundPool.play(soundId, left, right, 1, 0, 1f)
    }

    fun playSubtract(playerId: Int, enabled: Boolean) {
        if (!enabled) return
        val soundId = subtractSounds[playerId] ?: subtractSoundId
        val left = if (playerId % 2 == 0) 1f else 0.8f
        val right = if (playerId % 2 == 1) 1f else 0.8f
        soundPool.play(soundId, left, right, 1, 0, 0.94f)
    }

    fun playMilestone(score: Int, enabled: Boolean): Boolean {
        val soundId = milestoneSounds[score] ?: return false
        if (enabled) soundPool.play(soundId, 1f, 1f, 2, 0, 1f)
        return true
    }

    fun release() {
        soundPool.release()
    }
}
