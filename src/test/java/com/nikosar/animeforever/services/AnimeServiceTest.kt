package com.nikosar.animeforever.services

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.services.repository.AnimeRepository
import com.nikosar.animeforever.shikimori.Anime
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.ZonedDateTime

internal class AnimeServiceTest @Autowired constructor(
    private val animeRepository: AnimeRepository
) : AnimeForeverApplicationTests() {

    @Test
    fun save() {
        val animeService = AnimeService(animeRepository)
        val firstDate = ZonedDateTime.now()
        val anime = anime(firstDate)
        animeService.saveIfNotExist(anime).block()
        val secondDate = ZonedDateTime.now().plusDays(7)
        val animeForUpdate = anime(secondDate)
        animeService.save(animeForUpdate).block()

        StepVerifier.create(animeRepository.findByProviderId(113))
            .assertNext { assertEquals(it.nextEpisode!!.dayOfMonth, secondDate.dayOfMonth) }
            .verifyComplete()
    }

    private fun anime(zonedDateTime: ZonedDateTime) = mockk<Anime> {
        every { id } returns 113
        every { nextEpisodeAt } returns zonedDateTime
    }
}