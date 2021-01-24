package com.nikosar.animeforever.services

import com.nikosar.animeforever.mockSendMessage
import com.nikosar.animeforever.shikimori.AnimeProvider
import io.mockk.every
import io.mockk.mockk
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel
import reactor.core.publisher.Mono
import java.time.ZonedDateTime

fun mockAnimeProvider(): AnimeProvider {
    return mockk {
        every { findById(ANIME_ID_CALLED) } returns (
                Mono.just(
                    mockAnime(
                        ANIME_ID_CALLED,
                        NOTICEABLE_TIME,
                        "Onepunch",
                        2
                    )
                ))
        every { findById(ANIME_ID_NOT_CALLED) } returns (
                Mono.just(
                    mockAnime(
                        ANIME_ID_NOT_CALLED,
                        NOT_NOTICEABLE_TIME,
                        "Grandblue"
                    )
                ))
        every { findById(ANIME_ID_EPISODE_NOT_RELEASED) } returns (
                Mono.just(
                    mockAnime(
                        ANIME_ID_EPISODE_NOT_RELEASED,
                        NOT_NOTICEABLE_TIME,
                        "Grandblue",
                        1
                    )
                ))
    }
}

fun mockJda(): Pair<JDA, TextChannel> {
    val channelMockk = mockk<TextChannel> {
        mockSendMessage(this)
        every { guild } returns mockk(relaxed = true)
        every { name } returns "channel"
    }
    val jdaMockk = mockk<JDA> {
        every { getTextChannelById(any<Long>()) } returns channelMockk
    }
    return Pair(jdaMockk, channelMockk)
}

fun mockAnime(
    animeId: Long,
    nextEp: ZonedDateTime? = null,
    animeName: String = "any",
    episodesAiredNum: Int = 1
): com.nikosar.animeforever.shikimori.Anime {
    return mockk(relaxed = true) {
        every { id } returns animeId
        every { name } returns animeName
        every { russian } returns animeName
        every { nextEpisodeAt } returns nextEp
        every { episodesAired } returns episodesAiredNum
    }
}