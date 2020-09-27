package com.nikosar.animeforever.shikimori

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class ShikimoriImplTest @Autowired constructor(private val shikimori: Shikimori)
    : AnimeForeverApplicationTests() {
    @Test
    fun test() {
        val animeSearch = shikimori.animeSearch(AnimeSearch("Made in Abyss"))
        assertTrue(animeSearch.isNotEmpty())
    }
}