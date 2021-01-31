package com.nikosar.animeforever.services

import club.minnced.jda.reactor.toMono
import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.services.AnimeService
import com.nikosar.animeforever.discord.services.SubscriptionService
import com.nikosar.animeforever.discord.services.entity.Subscription
import com.nikosar.animeforever.discord.services.repository.AnimeRepository
import com.nikosar.animeforever.discord.services.repository.SubscriptionRepository
import com.nikosar.animeforever.shikimori.AnimeProvider
import io.mockk.*
import net.dv8tion.jda.api.entities.Message
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal open class SubscriptionServiceTest
@Autowired constructor(
    private val subscriptionService: SubscriptionService,
    private val subscriptionRepository: SubscriptionRepository,
    private val animeRepository: AnimeRepository,
    private val animeService: AnimeService,
    private val watchSites: Map<String, OnlineWatchWebsite>
) : AnimeForeverApplicationTests() {

    @BeforeEach
    fun init() {
        subscriptionRepository.findAll()
            .flatMap { subscriptionRepository.delete(it) }
            .blockLast()
        animeRepository.findAll()
            .flatMap { animeRepository.delete(it) }
            .blockLast()
    }

    @Test
    // not supported for test https://github.com/spring-projects/spring-framework/issues/24226
    //    @Transactional
    open fun successfulSubscribe() {
        val anime = mockAnime(1L, NOTICEABLE_TIME)
        val subscription = Subscription(
            null,
            3L,
            1L,
            1L
        )
        StepVerifier.create(subscriptionService.subscribe(subscription, anime))
            .assertNext { assertTrue(it.userId == 3L) }
            .verifyComplete()
    }

    @Test
    open fun releasesTest() {
        animeService.save(mockAnime(2L, NOTICEABLE_TIME)).block()
        animeService.save(mockAnime(3L, NOT_NOTICEABLE_TIME)).block()
        val subscription = Subscription(null, 1L, 2L, 1L)
        val subscription2 = Subscription(null, 2L, 3L, 1L)
        subscriptionRepository.save(subscription)
            .and(subscriptionRepository.save(subscription2))
            .block()
        StepVerifier.create(subscriptionRepository.newReleases(LocalDateTime.now()))
            .assertNext { assertEquals(it.userId, subscription.userId) }
            .verifyComplete()
    }

    @Test
    fun isSubscribed() {
        val mockAnime = mockAnime(1)
        val subscription = Subscription(null, 1, 1, 1)
        StepVerifier.create(subscriptionService.isSubscribed(subscription, mockAnime))
            .assertNext { assertFalse(it) }
            .verifyComplete()

        subscriptionService.subscribe(subscription, mockAnime).block()
        StepVerifier.create(subscriptionService.isSubscribed(subscription, mockAnime))
            .assertNext { assertTrue(it) }
            .verifyComplete()

    }

    @Test
    fun checkReleasesSuccessful() {
        initSubscriptionsForCheck()
        val animeProvider = mockAnimeProvider()
        val (jda, channel) = mockJda()
        SubscriptionService(subscriptionRepository, animeService, animeProvider, 10, watchSites, jda)
            .checkReleases()
        verify { animeProvider.findById(ANIME_ID_CALLED) }
        verify { animeProvider.findById(ANIME_ID_EPISODE_NOT_RELEASED) }
        verify { jda.getTextChannelById(CHANNEL_ID_555) }
        verify { jda.getTextChannelById(CHANNEL_ID_555) }
        verify { jda.getTextChannelById(CHANNEL_ID_600) }
        verify(exactly = 2) { channel.sendMessage(any<Message>()) }
        excludeRecords { channel.guild }
        excludeRecords { channel.name }
        excludeRecords { animeProvider.makeUrlFrom(any()) }

        confirmVerified(animeProvider, jda, channel)
    }

    @Test
    fun checkAnimeUpdatedAfterRelease() {
        initSubscriptionsForCheck()
        val nextEp = ZonedDateTime.now().plusDays(7)
        val animeProvider = mockk<AnimeProvider> {
            every { findById(ANIME_ID_CALLED) } returns
                    mockAnime(ANIME_ID_CALLED, nextEp, episodesAiredNum = 2).toMono()
            every { findById(ANIME_ID_EPISODE_NOT_RELEASED) } returns
                    mockAnime(ANIME_ID_CALLED, episodesAiredNum = 1).toMono()
            every { makeUrlFrom(any()) } returns "http://localhost.com"
        }
        val (jda, channel) = mockJda()
        SubscriptionService(subscriptionRepository, animeService, animeProvider, 10, watchSites, jda)
            .checkReleases()

        verify(exactly = 2) { channel.sendMessage(any<Message>()) }

        StepVerifier.create(animeService.findByProviderId(ANIME_ID_CALLED))
            .assertNext { assertEquals(nextEp.dayOfYear, it.nextEpisode!!.dayOfYear) }
            .verifyComplete()
    }


    private fun initSubscriptionsForCheck() {
        val animeWithNotice = mockAnime(ANIME_ID_CALLED, NOTICEABLE_TIME, episodesAiredNum = 1)
        val animeWithoutNotice = mockAnime(ANIME_ID_NOT_CALLED, NOT_NOTICEABLE_TIME)
        val animeNotYetReleased = mockAnime(ANIME_ID_EPISODE_NOT_RELEASED, NOTICEABLE_TIME)
        animeService.saveAll(listOf(animeWithNotice, animeWithoutNotice, animeNotYetReleased)).blockLast()
        val subs = listOf(
            Subscription(null, 100, ANIME_ID_CALLED, CHANNEL_ID_555),
            Subscription(null, 101, ANIME_ID_CALLED, CHANNEL_ID_555),
            Subscription(null, 102, ANIME_ID_CALLED, CHANNEL_ID_600),
            Subscription(null, 103, ANIME_ID_NOT_CALLED, CHANNEL_ID_555),
            Subscription(null, 104, ANIME_ID_EPISODE_NOT_RELEASED, CHANNEL_ID_556),
        )
        subscriptionRepository.saveAll(subs).blockLast()
    }


}

const val ANIME_ID_CALLED = 444L
const val ANIME_ID_NOT_CALLED = 455L
const val ANIME_ID_EPISODE_NOT_RELEASED = 465L


const val CHANNEL_ID_555 = 555L
const val CHANNEL_ID_556 = 556L
const val CHANNEL_ID_600 = 600L

val NOTICEABLE_TIME: ZonedDateTime = LocalDateTime.now().minusHours(1L).atZone(ZoneId.systemDefault())
val NOT_NOTICEABLE_TIME: ZonedDateTime = LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault())