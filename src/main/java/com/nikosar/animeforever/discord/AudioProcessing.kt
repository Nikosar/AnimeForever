package com.nikosar.animeforever.discord

import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import kotlin.math.pow

fun calculateRMS(audioData: ShortArray): Double {
    var squareSum = 0.0

    for (byte in audioData) {
        squareSum += byte.toDouble().pow(2)
    }
    val avgMeanSquare = squareSum / audioData.size
    return avgMeanSquare.pow(0.5)
}


fun rms(audioData: ShortArray): Double {
    val dataArray = Nd4j.create(audioData, longArrayOf(1, audioData.size.toLong()), DataType.DOUBLE)
    return rms(dataArray)
}

fun rms(dataArray: INDArray?): Double {
    val avgMeanSquare = Transforms.pow(dataArray, 2.0).meanNumber().toDouble()
    return avgMeanSquare.pow(0.5)

}
