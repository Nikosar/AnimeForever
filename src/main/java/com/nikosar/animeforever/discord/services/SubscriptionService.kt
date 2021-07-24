package com.nikosar.animeforever.discord.services

import club.minnced.jda.reactor.asMono
import club.minnced.jda.reactor.toMono
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.messages.newEpisodeIsOut
import com.nikosar.animeforever.discord.services.entity.Subscription
import com.nikosar.animeforever.discord.services.repository.SubscriptionRepository
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
    @Value("\${notification.delay}")
    private val delay: Long,
    private val watchSites: Map<String, OnlineWatchWebsite>,
    private val jda: JDA
) {
    private val logger: Logger = LoggerFactory.getLogger(SubscriptionService::class.java)

    @Transactional
    open fun subscribe(subscription: Subscription, anime: Anime): Mono<Subscription> {
        return animeService.saveIfNotExist(anime)
            .flatMap { subscriptionRepository.save(subscription) }
    }

    open fun isSubscribed(subscription: Subscription, anime: Anime): Mono<Boolean> {
        return subscriptionRepository.findSubscription(
            anime.id,
            subscription.channelId,
            subscription.userId
        ).map { true }
            .defaultIfEmpty(false)
    }

    open fun unsubscribe(user: User, anime: Anime): Mono<Void> {
        return subscriptionRepository.deleteAllByUserIdAndAnimeId(user.idLong, anime.id)
    }

    @Scheduled(cron = "0 */10 * * * *")
    open fun checkReleases() {
        logger.debug("Checking out releases")
        val dateWithDelay = LocalDateTime.now().minusMinutes(delay)
        subscriptionRepository.newReleasesFrom(dateWithDelay)
            .groupBy { subscription -> subscription.animeId }
            .flatMap { groupedByAnime -> prepareNotices(groupedByAnime) }
            .subscribe()
    }

    private fun prepareNotices(groupedByAnime: GroupedFlux<Long, Subscription>) =
        animeProvider.findById(groupedByAnime.key())
            .flatMap { anime ->
                animeService.findByProviderId(anime.id)
                    .flatMap { Pair(anime, it).toMono() }
            }
            .flatMap { pair -> animeService.save(pair.first).thenReturn(pair) }
            .filterWhen { (anime, dbAnime) -> (isNewEpisodeReleased(dbAnime, anime)).toMono() }
            .flatMapMany { (anime) -> noticeForChannels(groupedByAnime, anime) }

    private fun isNewEpisodeReleased(
        dbAnime: com.nikosar.animeforever.discord.services.entity.Anime,
        anime: Anime
    ) = dbAnime.noticedEpisode != null && (dbAnime.noticedEpisode < anime.episodesAired)

    private fun noticeForChannels(
        groupedByAnime: GroupedFlux<Long, Subscription>,
        anime: Anime
    ) = groupedByAnime.groupBy { it.channelId }
        .flatMap { it.collectList() }
        .flatMap { createMessage(it, anime) }


    //TODO probably move to message creator
    private fun createMessage(
        subscriptionsByChannel: MutableList<Subscription>,
        anime: Anime
    ): Mono<Message> {
        val channelId = subscriptionsByChannel.first().channelId
        val usersMentions = usersMentions(subscriptionsByChannel)
        val createWatchMessageWithMentions =
            newEpisodeIsOut(usersMentions, anime, animeProvider, watchSites)
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

    fun list(user: User, page: Int) {
        subscriptionRepository.findAll()
//            .grou

    }
}
