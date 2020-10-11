package com.nikosar.animeforever.discord

import kotlin.math.pow

fun rms(audioData: ShortArray): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.toDouble().pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}

fun rms(audioData: DoubleArray): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}
