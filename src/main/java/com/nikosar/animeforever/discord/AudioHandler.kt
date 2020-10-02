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
import kotlin.math.pow

class AudioHandler : AudioReceiveHandler, AudioSendHandler {
    private val logger: Logger = LoggerFactory.getLogger(AudioHandler::class.java)

    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private val list = mutableListOf<Byte>()

    override fun handleUserAudio(userAudio: UserAudio) {
        logger.info("user audio")
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

    private fun bytesToDouble(audioData: ByteArray): DoubleArray {
        val doubleArray = DoubleArray(audioData.size / 2)
        for (i: Int in doubleArray.indices) {
            doubleArray[i] = (audioData[i * 2].toInt() shl 4 + audioData[i * 2 + 1].toInt()).toDouble()
        }
        return doubleArray
    }

    fun calculateRMS(audioData: DoubleArray): Double {
        var squareSum = 0.0

        for (byte in audioData) {
            squareSum += byte.pow(2)
        }
        val avgMeanSquare = squareSum / audioData.size
        return avgMeanSquare.pow(0.5)
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