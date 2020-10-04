package com.nikosar.animeforever.shikimori

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SeasonTest {
    @Test
    fun correctSeasonTest() {
        assertEquals("fall", fromLocalDate(LocalDate.of(2020, 10, 4)).shikiSeason)
        assertEquals("summer", fromLocalDate(LocalDate.of(2020, 7, 4)).shikiSeason)
        assertEquals("spring", fromLocalDate(LocalDate.of(2020, 4, 4)).shikiSeason)
        assertEquals("winter", fromLocalDate(LocalDate.of(2020, 1, 4)).shikiSeason)
    }
}