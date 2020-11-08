package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.mockEvent
import com.nikosar.animeforever.shikimori.AnimeProvider
import com.nikosar.animeforever.shikimori.Page
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class AnimeSearchCommandTest {

    @Test
    fun ongoings() {
        val watchWebsite = mockk<OnlineWatchWebsite>()
        val animeProvider = mockk<AnimeProvider>()
        val animeSearchCommand = AnimeSearchCommand(animeProvider, watchWebsite)

        every { animeProvider.ongoings(any()) } returns Mono.just(emptyList())
        val event = mockEvent()
        animeSearchCommand.ongoings(event, 2, 20)
        verify { animeProvider.ongoings(Page(2, 20)) }
    }
}