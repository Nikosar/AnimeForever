package com.nikosar.animeforever.services

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.mockSendMessage
import com.nikosar.animeforever.shikimori.AnimeProvider
import io.mockk.every
import io.mockk.mockk
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

internal open class SubscriptionServiceTest
@Autowired constructor(
    private val subscriptionService: SubscriptionService,
    private val subscriptionRepository: SubscriptionRepository,
    private val animeRepository: AnimeRepository,
    private val watchSites: Map<String, OnlineWatchWebsite>
) : AnimeForeverApplicationTests() {

    @Test
    // not supported for test https://github.com/spring-projects/spring-framework/issues/24226
    //    @Transactional
    open fun successfulSubscribe() {
        val anime = Anime(null, 1L, LocalDateTime.now())
        val subscription = Subscription(
            null,
            3L,
            1L,
            true,
            1L
        )
        StepVerifier.create(
            subscriptionService.subscribe(subscription, anime).flatMap {
                subscriptionRepository.findById(1L)
            })
            .assertNext { assertTrue(it?.id == 1L) }
            .verifyComplete()
    }

    @Test
    open fun releasesTest() {
        animeRepository.save(Anime(null, 2L, LocalDateTime.now().minusHours(1L))).block()
        animeRepository.save(Anime(null, 3L, LocalDateTime.now().plusDays(1L))).block()
        val subscription = Subscription(null, 1L, 2L, true, 1L)
        val subscription2 = Subscription(null, 2L, 3L, true, 1L)
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
        val animeProvider = mockk<AnimeProvider>()
        val jda = mockk<JDA>()
        val textChannel = mockk<TextChannel>()
        every { animeProvider.findById(444) } returns (Mono.just(createAnimeMock(444, "One punch")))
        every { animeProvider.findById(455) } returns (Mono.just(createAnimeMock(455, "Grand blue")))
        every { jda.getTextChannelById(any<Long>()) } returns textChannel
        every { jda.getUserById(100L) }.returns(userMock("Vasya"))
        every { jda.getUserById(101L) }.returns(userMock("Petya"))
        every { jda.getUserById(102L) }.returns(userMock("Tester"))
        every { jda.getUserById(103L) }.returns(userMock("Toster"))
        mockSendMessage(textChannel)
        SubscriptionService(subscriptionRepository, animeRepository, animeProvider, watchSites, jda)
    }

    private fun createAnimeMock(id: Long, name: String) = com.nikosar.animeforever.shikimori.Anime(
        id,
        name = name,
        russian = name,
        url = "",
        episodes = 12,
        episodesAired = 13,
        favoured = true,
        anons = true,
        ongoing = true,
        threadId = 10,
        topicId = 10,
        myanimelistId = 100,
        image = null,
        kind = null,
        status = null,
        airedOn = null,
        releasedOn = null,
        rating = null,
        licenseNameRu = null,
        duration = 100,
        description = null,
        descriptionSource = null,
        franchise = null,
        genres = null,
        updatedAt = null,
        nextEpisodeAt = null
    )

    private fun userMock(username: String): User {
        val user = mockk<User>()
        every { user.asMention } returns ("@$username")
        return user
    }

    private fun initSubscriptionsForCheck() {
        val animeWithNotice = Anime(null, 444L, LocalDateTime.now().minusHours(1L))
        val animeWithoutNotice = Anime(null, 455L, LocalDateTime.now().plusDays(1L))
        listOf(animeWithNotice)
        val subs = listOf(
            Subscription(null, 100L, 444L, true, 555L),
            Subscription(null, 101L, 444L, true, 555L),
            Subscription(null, 102L, 444L, true, 600L),
            Subscription(null, 103L, 455L, true, 555L),
        )
        subscriptionRepository.saveAll(subs)
    }

}