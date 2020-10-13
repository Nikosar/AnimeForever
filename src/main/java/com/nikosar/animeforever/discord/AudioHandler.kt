package com.nikosar.animeforever.discord

import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.audio.UserAudio
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AudioHandler : AudioReceiveHandler {
    private val logger: Logger = LoggerFactory.getLogger(AudioHandler::class.java)
    private val levels = mutableListOf<Double>()
    var sessionLevel = 0.0
        private set

    override fun handleUserAudio(userAudio: UserAudio) {
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        if (combinedAudio.users.isEmpty()) {
            return;
        }
        val audioData = combinedAudio.getAudioData(1.0)
        levels.add(rms(read_16bit(audioData)))

        if (levels.size >= 50) {
            val rms = rms(*levels.toDoubleArray())
            logger.info("Loudness level for last second = $rms")
            levels.clear()
            sessionLevel = rms(sessionLevel, rms)
        }

    }

    override fun canReceiveCombined(): Boolean {
        return levels.size < 200
    }

    override fun canReceiveUser(): Boolean {
        return false
    }
}