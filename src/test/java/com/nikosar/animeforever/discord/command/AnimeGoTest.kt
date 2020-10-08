package com.nikosar.animeforever.discord.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AnimeGoTest {

    @Test
    fun makeUrlFrom() {
        val makeUrlFrom = AnimeGo("https://animego.org/search/all")
                .makeUrlFrom("Haikyuu!!: To the Top 2nd Season")
        assertEquals("https://animego.org/search/all?q=Haikyuu%21%21%3A+To+the+Top+2nd+Season", makeUrlFrom)
    }
}