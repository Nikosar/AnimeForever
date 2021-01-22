package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.discord.command.processor.Sequential
import com.nikosar.animeforever.discord.command.utils.animeListMessage
import com.nikosar.animeforever.discord.command.utils.createFindMessage
import com.nikosar.animeforever.discord.command.utils.createWatchMessage
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.Year

@BotCommander
class AnimeSearchCommand(
        private val animeProvider: AnimeProvider,
        private val watchSites: Map<String, OnlineWatchWebsite>
) {
    @BotCommand(value = ["-f", "find"], description = "find anime with max rating by query")
    fun findAnime(event: MessageReceivedEvent, search: String): Flux<*> =
            animeProvider.search(AnimeSearch(search))
                    .filter { it.isNotEmpty() }
                    .map { it[0] }
                    .flatMap { animeProvider.findById(it.id) }
                    .flatMapMany {
                        Flux.just(
                            createFindMessage(it, animeProvider),
                            createWatchMessage(it, watchSites)
                        )
                    }
                    .defaultIfEmpty(MessageBuilder(CRAB).build())
                    .flatMap { event.channel.sendMessage(it).asMono() }


    @BotCommand(["on", "ongoings"], description = "find top anime of current season")
    @Sequential
    fun ongoings(event: MessageReceivedEvent, page: Int = 1, size: Int = 20): Mono<*> =
            best(event, Year.now().value, fromLocalDate(LocalDate.now()), page, size)


    @BotCommand(["best"], description = "best anime for given year and season")
    @Sequential
    fun best(event: MessageReceivedEvent, year: Int? = null,
             season: Season? = null, page: Int = 1, size: Int = 20): Mono<*> =
        animeProvider.search(AnimeSearch(season = season, year = year), Page(page, size))
            .map { animeListMessage(it, page, size) }
                    .flatMap { event.channel.sendMessage(it).asMono() }


    @BotCommand(["bring"], visible = false)
    fun coffeeCommand(event: MessageReceivedEvent, args: String): Mono<*> {
        return if (args == "me coffee, please") {
            event.channel.sendMessage("Your coffee :coffee:").asMono()
        } else {
            Mono.empty<String>()
        }
    }
}

const val BEST_PINK_COLOR = 16712698
const val WATCH = "Смотреть"
const val ONGOINGS = "Онгоинги"
const val RATING = "Рейтинг"
const val EPISODES = "Эпизоды"
const val GENRES = "Жанры"
const val RATING_EMOJI = ":star:"
const val CRAB = ":crab:"