package com.nikosar.animeforever.discord

import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.audio.UserAudio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedQueue

class AudioHandler : AudioReceiveHandler, AudioSendHandler {
    private val logger: Logger = LoggerFactory.getLogger(AudioHandler::class.java)

    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private val list = mutableListOf<Byte>()

    override fun handleUserAudio(userAudio: UserAudio) {
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        if (combinedAudio.users.isEmpty()) {
            return;
        }
        val audioData = combinedAudio.getAudioData(1.0)

        if (list.size <= 380000) {
            list.addAll(audioData.asSequence())
        } else {
            list.addAll(audioData.asSequence())
            val outputFormat = AudioReceiveHandler.OUTPUT_FORMAT
            val file = Paths.get("test").toFile()
            if (!file.exists()) {
                file.createNewFile()
            }
            FileOutputStream(file).write(list.toByteArray())
        }
//        val rms = calculateRMS(bytesToDouble(audioData))
//
//        if (rms > 10) {
//            logger.info("Audio level is {}", rms)
//        }
//        queue.add(audioData)
    }


    override fun canReceiveCombined(): Boolean {
        return list.size < 384000
    }

//    override fun includeUserInCombinedAudio(user: User): Boolean {
//        return true
//    }

    override fun canReceiveUser(): Boolean {
        return false
    }

    override fun canProvide(): Boolean {
        return queue.isNotEmpty()
    }

    override fun provide20MsAudio(): ByteBuffer? {
        val data = queue.poll()
        return data.let { ByteBuffer.wrap(it) }
    }


}