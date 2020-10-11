package com.nikosar.animeforever.discord

import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.audio.UserAudio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

class AudioHandler : AudioReceiveHandler {
    private val logger: Logger = LoggerFactory.getLogger(AudioHandler::class.java)
    private val levels = mutableListOf<Double>()

    override fun handleUserAudio(userAudio: UserAudio) {
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        if (combinedAudio.users.isEmpty()) {
            return;
        }
        val audioData = combinedAudio.getAudioData(1.0)
        levels.add(rms(readStereo(audioData)))

        if (levels.size >= 50) {
            logger.info("Loudness level for last second = ${rms(levels.toDoubleArray())}")
            levels.clear()
        }

    }

    fun readStereo(byteArray: ByteArray): ShortArray {
        val shortArray = ShortArray(byteArray.size / 2)
        ByteBuffer.wrap(byteArray).asShortBuffer().get(shortArray)
        return shortArray
    }

    override fun canReceiveCombined(): Boolean {
        return true
    }

    override fun canReceiveUser(): Boolean {
        return false
    }


}