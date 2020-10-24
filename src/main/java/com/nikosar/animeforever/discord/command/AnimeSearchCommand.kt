package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Mono
import java.time.LocalDate

@BotCommander
class AnimeSearchCommand(
        private val animeProvider: AnimeProvider,
        @Value("\${shikimori.api}")
        private val shikimoriApi: String,
        private val onlineWatchWebsite: OnlineWatchWebsite
) {
    @BotCommand(value = ["-f", "find"], description = "find anime with max rating by query")
    fun findAnime(args: String, event: MessageReceivedEvent): Mono<*> {
        return animeProvider.search(AnimeSearch(args))
                .map { createFindEmbed(it.getOrNull(0)) }
                .flatMap { event.channel.sendMessage(it).asMono() }
    }

    private fun createFindEmbed(anime: Anime?): Message {
        val messageBuilder = MessageBuilder()
        return if (anime != null) {
            messageBuilder.setContent(buildUrl(anime))
            messageBuilder.setEmbed(
                    EmbedBuilder().setColor(BEST_PINK_COLOR)
                            .setTitle("$WATCH -> ${anime.russian}", onlineWatchWebsite.makeUrlFrom(anime.name)).build()
            )
            messageBuilder.build()
        } else {
            messageBuilder.setContent(":crab:").build()
        }
    }

    @BotCommand(["on", "ongoings"], description = "find top 10 anime of current season")
    fun ongoings(args: String, event: MessageReceivedEvent): Mono<*> = requestOngoings()
            .map {
                val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR).setTitle(ONGOINGS)
                it.forEachIndexed { i, anime ->
                    anime.apply {
                        embedBuilder.addField("${i + 1}. $russian",
                                "$score:star:  ep:$episodesAired/$episodes",
                                false)
                    }
                }
                embedBuilder.build()
            }
            .flatMap { event.channel.sendMessage(it).asMono() }

    private fun requestOngoings(): Mono<List<Anime>> {
        val localDate = LocalDate.now()
        val year = localDate.year
        val season = fromLocalDate(localDate)

        val search = AnimeSearch(season = "${season.shikiSeason}_$year")
        return animeProvider.search(search, Page(1, 10))
    }

    @BotCommand(["bring"], visible = false)
    fun coffeeCommand(args: String, event: MessageReceivedEvent): Mono<*> {
        return if (args == "me coffee, please") {
            event.channel.sendMessage("Your coffee :coffee:").asMono()
        } else {
            Mono.empty<String>()
        }
    }

    private fun buildUrl(anime: Anime) = shikimoriApi + anime.url
}

const val BEST_PINK_COLOR = 16712698
const val WATCH = "Смотреть"
const val ONGOINGS = "Онгоинги"