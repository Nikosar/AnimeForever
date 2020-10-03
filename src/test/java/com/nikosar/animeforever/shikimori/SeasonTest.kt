package com.nikosar.animeforever.shikimori

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SeasonTest {
    @Test
    fun test() {
        val fromLocalDate = fromLocalDate(LocalDate.now())
        assertEquals("fall", fromLocalDate.shikiSeason)
    }
}