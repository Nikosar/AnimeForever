package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.services.Subscription
import com.nikosar.animeforever.services.SubscriptionService
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import com.nikosar.animeforever.shikimori.AnimeSearch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.CorePublisher
import reactor.core.publisher.Mono
import java.time.ZoneId

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
            .map {
                subscribe(event, it)
            }.doOnError { TODO() }
    }

    private fun subscribe(event: MessageReceivedEvent, anime: Anime): Mono<Subscription> {

        return subscriptionService.subscribe(
            Subscription(
                null,
                event.author.idLong,
                anime.id,
                event.isFromGuild,
                event.channel.idLong,
            ),
            com.nikosar.animeforever.services.Anime(
                null, anime.id, anime.nextEpisodeAt
                    ?.withZoneSameLocal(ZoneId.systemDefault())
                    ?.toLocalDateTime() ?: TODO()
            )
        )
    }

//    private fun subscribeGuild(event: MessageReceivedEvent, anime: Anime) {
//        subscriptionService.subscribe(
//            Subscription(
//                null,
//                event.author.idLong,
//                anime.id,
//                event.isFromGuild,
//                event.channel.idLong
//            )
//        )
//    }
}