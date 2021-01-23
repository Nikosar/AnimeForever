package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.discord.command.utils.subscribeAlreadyOutMessage
import com.nikosar.animeforever.discord.command.utils.subscribePrivateChannel
import com.nikosar.animeforever.discord.command.utils.subscribeSuccessfulMessage
import com.nikosar.animeforever.services.SubscriptionService
import com.nikosar.animeforever.services.entity.Subscription
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import com.nikosar.animeforever.shikimori.AnimeSearch
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
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
            .flatMap { subscribeController(event, it) }
            .flatMap { event.channel.sendMessage(it).asMono() }
            .doOnError { event.channel.sendMessage("nope").asMono() }
    }

    private fun subscribeController(event: MessageReceivedEvent, anime: Anime): Mono<Message> {
        if (event.isFromType(ChannelType.PRIVATE)) {
            return Mono.just(subscribePrivateChannel(event.author))
        }
        if (!anime.ongoing || anime.episodes == anime.episodesAired) {
            return Mono.just(subscribeAlreadyOutMessage(event.author, anime))
        }
        return subscriptionService.subscribe(
            Subscription(
                null,
                event.author.idLong,
                anime.id,
                event.channel.idLong,
            ),
            anime
        ).flatMap { Mono.just(subscribeSuccessfulMessage(event.author, anime)) }

    }
}