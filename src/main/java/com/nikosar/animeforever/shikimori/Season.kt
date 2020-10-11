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
        DECEMBER, JANUARY, FEBRUARY -> Season.WINTER
        MARCH, APRIL, MAY -> Season.SPRING
        JUNE, JULY, AUGUST -> Season.SUMMER
        SEPTEMBER, OCTOBER, NOVEMBER -> Season.FALL
    }
}