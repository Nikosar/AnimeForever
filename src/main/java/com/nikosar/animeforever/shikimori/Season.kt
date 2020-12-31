package com.nikosar.animeforever.shikimori

import java.time.LocalDate
import java.time.Month.*

enum class Season {
    WINTER,
    SPRING,
    SUMMER,
    FALL;

    val shikiSeason: String
        get() = name.toLowerCase()
}

fun fromLocalDate(localDate: LocalDate): Season {
    return when (localDate.month) {
        JANUARY, FEBRUARY, MARCH -> Season.WINTER
        APRIL, MAY, JUNE -> Season.SPRING
        JULY, AUGUST, SEPTEMBER -> Season.SUMMER
        OCTOBER, NOVEMBER, DECEMBER -> Season.FALL
    }
}