package com.nikosar.animeforever.discord.commands

import club.minnced.jda.reactor.asMono
import club.minnced.jda.reactor.toMono
import com.nikosar.animeforever.discord.commandapi.api.BotCommand
import com.nikosar.animeforever.discord.commandapi.api.BotCommander
import com.nikosar.animeforever.discord.messages.*
import com.nikosar.animeforever.discord.services.SubscriptionService
import com.nikosar.animeforever.discord.services.entity.Subscription
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import com.nikosar.animeforever.shikimori.AnimeSearch
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.CorePublisher
import reactor.core.publisher.Mono

@BotCommander
class SubscribeCommand(
    private val animeProvider: AnimeProvider,
    private val subscriptionService: SubscriptionService
) {
    @BotCommand(["subscribe", "sub"], description = "Subscribe to receive notification about new series")
    fun subscribe(event: MessageReceivedEvent, search: String): CorePublisher<*> {
        return animeProvider.search(AnimeSearch(search))
            .filter { it.isNotEmpty() }
            .map { it[0] }
            .flatMap { animeProvider.findById(it.id) }
            .flatMap { trySubscribe(event, it) }
            .flatMap { event.channel.sendMessage(it).asMono() }
    }

    @BotCommand(["unsubscribe", "unsub"], description = "Unsubscribe from notification to anime")
    fun unsubscribe(event: MessageReceivedEvent, search: String): CorePublisher<*> {
        return animeProvider.search(AnimeSearch(search))
            .filter { it.isNotEmpty() }
            .map { it[0] }
            .flatMap { tryUnsubscribe(event, it) }
            .flatMap { event.channel.sendMessage(it).asMono() }
    }

    //    @BotCommand(["sublist", "subl"], description = "Shows list of your subscriptions  subl [page]")
//    @Sequential
    fun list(event: MessageReceivedEvent, page: Int) {
//        return subscriptionService.list(event.author, page)

    }

    private fun tryUnsubscribe(event: MessageReceivedEvent, anime: Anime): Mono<Message> {
        val user = event.author
        val subscription = createSubscription(user, anime, event)

        return subscriptionService.isSubscribed(subscription, anime)
            .flatMap { isSubscribed ->
                if (isSubscribed) {
                    subscriptionService.unsubscribe(user, anime)
                        .thenReturn(unsubscribedSuccessfully(user, anime))
                } else {
                    alreadyUnsubscribed(user, anime).toMono()
                }
            }
    }

    private fun trySubscribe(event: MessageReceivedEvent, anime: Anime): Mono<Message> {
        val user = event.author
        if (event.isFromType(ChannelType.PRIVATE)) {
            return subscribePrivateChannel(user).toMono()
        }
        if (!anime.ongoing || anime.episodes == anime.episodesAired) {
            return alreadyAired(user, anime).toMono()
        }
        val subscription = createSubscription(user, anime, event)

        return subscriptionService.isSubscribed(subscription, anime)
            .flatMap { isSubscribed ->
                if (isSubscribed) {
                    alreadySubscribed(user, anime).toMono()
                } else {
                    subscriptionService.subscribe(subscription, anime)
                        .thenReturn(subscribeSuccessful(user, anime))
                }
            }
    }

    private fun createSubscription(
        user: User,
        anime: Anime,
        event: MessageReceivedEvent
    ): Subscription {
        return Subscription(
            null,
            user.idLong,
            anime.id,
            event.channel.idLong,
        )
    }
}