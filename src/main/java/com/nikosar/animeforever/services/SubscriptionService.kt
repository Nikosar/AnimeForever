package com.nikosar.animeforever.services

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.command.utils.newEpisodeIsOut
import com.nikosar.animeforever.services.entity.Subscription
import com.nikosar.animeforever.services.repository.SubscriptionRepository
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
open class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val animeService: AnimeService,
    private val animeProvider: AnimeProvider,
    private val watchSites: Map<String, OnlineWatchWebsite>,
    private val jda: JDA
) {
    private val logger: Logger = LoggerFactory.getLogger(SubscriptionService::class.java)

    @Transactional
    open fun subscribe(subscription: Subscription, anime: Anime): Mono<Subscription> {
        return animeService.saveIfNotExist(anime)
            .flatMap { subscriptionRepository.save(subscription) }
    }

    @Scheduled(cron = "* */10 * * * *")
    open fun checkReleases() {
        logger.debug("Checking out releases")
        subscriptionRepository.newReleases(LocalDateTime.now())
            .groupBy { subscription -> subscription.animeId }
            .flatMap { subscriptionGroup -> messageForAnime(subscriptionGroup) }
            .subscribe()
    }

    private fun messageForAnime(subscriptionGroup: GroupedFlux<Long, Subscription>) =
        animeProvider.findById(subscriptionGroup.key() ?: TODO("never?"))
            .flatMapMany { anime ->
                animeService.save(anime)
                    .thenMany(subscriptionGroup.groupBy { it.channelId }
                        .flatMap { it.collectList() }
                        .flatMap { createMessage(it, anime) })
            }

    private fun createMessage(
        subscriptionsByChannel: MutableList<Subscription>,
        anime: Anime
    ): Mono<Message> {
        val channelId = subscriptionsByChannel.first().channelId
        val usersMentions = usersMentions(subscriptionsByChannel)
        val createWatchMessageWithMentions =
            newEpisodeIsOut(usersMentions, anime, watchSites)
        val textChannelById = jda.getTextChannelById(channelId)
        if (textChannelById == null) {
            logger.info("Can't access text channel with id: $channelId")
        } else {
            logger.debug("Preparing message to send to ${textChannelById.guild.apply { "$name-$id" }}, channel ${textChannelById.name}")
        }
        return textChannelById?.sendMessage(createWatchMessageWithMentions)
            ?.asMono() ?: Mono.empty()
    }

    private fun usersMentions(subscriptionsByChannel: MutableList<Subscription>) =
        subscriptionsByChannel
            .map { "<@${it.userId}>" }
            .joinToString(" ") { it }
}