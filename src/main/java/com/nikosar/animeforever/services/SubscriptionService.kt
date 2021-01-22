package com.nikosar.animeforever.services

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.command.utils.createWatchMessageWithMentions
import com.nikosar.animeforever.shikimori.AnimeProvider
import net.dv8tion.jda.api.JDA
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
open class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val animeRepository: AnimeRepository,
    private val animeProvider: AnimeProvider,
    private val watchSites: Map<String, OnlineWatchWebsite>,
    private val jda: JDA
) {
    @Transactional
    open fun subscribe(subscription: Subscription, anime: Anime): Mono<Subscription> {
        return animeRepository.findByProviderId(anime.providerId)
            .switchIfEmpty(animeRepository.save(anime))
            .flatMap { subscriptionRepository.save(subscription) }
    }

    @Scheduled(cron = "0 0 */1 * * *")
    fun checkReleases() {
        subscriptionRepository.newReleases(LocalDateTime.now())
            .groupBy { subscription -> subscription.animeId }
            .flatMap { subscriptionGroup -> createMessageForEachAnime(subscriptionGroup) }
            .subscribe()
    }

    private fun createMessageForEachAnime(subscriptionGroup: GroupedFlux<Long, Subscription>) =
        animeProvider.findById(subscriptionGroup.key() ?: TODO("never?"))
            .flatMapMany { anime ->
                subscriptionGroup.groupBy { it.channelId }
                    .flatMap {
                        it.collectList()
                    }.flatMap { subscriptionsByChannel ->
                        val channelId = subscriptionsByChannel.first().channelId
                        val usersMentions = usersMentions(subscriptionsByChannel)
                        jda.getTextChannelById(channelId)
                            ?.sendMessage(createWatchMessageWithMentions(usersMentions, anime, watchSites))
                            ?.asMono() ?: Mono.empty()
                    }
            }

    private fun usersMentions(subscriptionsByChannel: MutableList<Subscription>) =
        subscriptionsByChannel
            .mapNotNull { jda.getUserById(it.userId)?.asMention }
            .joinToString(" ") { it }
}