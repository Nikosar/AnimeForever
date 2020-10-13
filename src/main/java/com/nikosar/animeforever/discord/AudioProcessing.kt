package com.nikosar.animeforever.discord

import java.nio.ByteBuffer
import kotlin.math.pow

fun rms(audioData: ShortArray): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.toDouble().pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}

fun rms(vararg audioData: Double): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}

fun read_16bit(byteArray: ByteArray): ShortArray {
    val shortArray = ShortArray(byteArray.size / 2)
    ByteBuffer.wrap(byteArray).asShortBuffer().get(shortArray)
    return shortArray
}
