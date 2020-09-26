package com.nikosar.animeforever.shikimori

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class ShikimoriServiceImplTest @Autowired constructor(private val shikimoriService: ShikimoriService)
    : AnimeForeverApplicationTests() {
    @Test
    fun test() {
        val animeSearch = shikimoriService.animeSearch(AnimeSearch("Made in Abyss"))
        val anime = animeSearch[0]
        assertNotNull(anime)
    }
}