package com.nikosar.animeforever.services

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.mockSendMessage
import com.nikosar.animeforever.services.entity.Subscription
import com.nikosar.animeforever.services.repository.SubscriptionRepository
import com.nikosar.animeforever.shikimori.AnimeProvider
import io.mockk.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal open class SubscriptionServiceTest
@Autowired constructor(
    private val subscriptionService: SubscriptionService,
    private val subscriptionRepository: SubscriptionRepository,
    private val animeService: AnimeService,
    private val watchSites: Map<String, OnlineWatchWebsite>
) : AnimeForeverApplicationTests() {

    @BeforeEach
    fun init() {
        subscriptionRepository.findAll()
            .flatMap { subscriptionRepository.delete(it) }
            .blockLast()
    }

    @Test
    // not supported for test https://github.com/spring-projects/spring-framework/issues/24226
    //    @Transactional
    open fun successfulSubscribe() {
        val anime = createAnimeMock(1L, NOTICEABLE_TIME)
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
        animeService.save(createAnimeMock(2L, NOTICEABLE_TIME)).block()
        animeService.save(createAnimeMock(3L, NOT_NOTICEABLE_TIME)).block()
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
    fun checkReleasesSuccessful() {
        initSubscriptionsForCheck()
        val animeProvider = mockAnimeProvider()
        val animeService = spyk(this.animeService)
        val (jda, channel) = mockJda()
        SubscriptionService(subscriptionRepository, animeService, animeProvider, watchSites, jda)
            .checkReleases()
        verify { animeProvider.findById(444) }
        verify { jda.getTextChannelById(555) }
        verify { jda.getTextChannelById(600) }
        verify(exactly = 2) { channel.sendMessage(any<Message>()) }
        verify { animeService.save(any()) }
        excludeRecords { channel.guild }
        excludeRecords { channel.name }

        confirmVerified(animeProvider, jda, channel)
    }

    private fun mockAnimeProvider(): AnimeProvider {
        return mockk {
            every { findById(444) } returns (
                    Mono.just(
                        createAnimeMock(
                            444,
                            NOTICEABLE_TIME,
                            "Onepunch"
                        )
                    ))
            every { findById(455) } returns (
                    Mono.just(
                        createAnimeMock(
                            455,
                            NOT_NOTICEABLE_TIME,
                            "Grandblue"
                        )
                    ))
        }
    }

    private fun mockJda(): Pair<JDA, TextChannel> {
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

    private fun createAnimeMock(
        animeId: Long,
        nextEp: ZonedDateTime,
        animeName: String = "any"
    ): com.nikosar.animeforever.shikimori.Anime {
        return mockk(relaxed = true) {
            every { id } returns animeId
            every { name } returns animeName
            every { russian } returns animeName
            every { nextEpisodeAt } returns nextEp
        }
    }

    private fun initSubscriptionsForCheck() {
        val animeWithNotice = createAnimeMock(444L, NOTICEABLE_TIME)
        val animeWithoutNotice = createAnimeMock(455L, NOT_NOTICEABLE_TIME)
        animeService.saveAll(listOf(animeWithNotice, animeWithoutNotice)).blockLast()
        val subs = listOf(
            Subscription(null, 100, 444, 555),
            Subscription(null, 101, 444, 555),
            Subscription(null, 102, 444, 600),
            Subscription(null, 103, 455, 555),
        )
        subscriptionRepository.saveAll(subs).blockLast()
    }


}

private val NOTICEABLE_TIME = LocalDateTime.now().minusHours(1L).atZone(ZoneId.systemDefault())
private val NOT_NOTICEABLE_TIME = LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault())