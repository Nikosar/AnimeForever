package com.nikosar.animeforever.discord

import kotlin.math.pow

fun calculateRMS(audioData: ShortArray): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.toDouble().pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}
