package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Mono
import java.time.LocalDate

@BotCommander
class ShikiCommand(
        private val shikimori: Shikimori,
        @Value("\${shikimori.api}")
        private val shikimoriApi: String
) {
    @BotCommand(value = ["!f"], description = "find anime with max rating by query")
    fun findAnime(args: String, event: MessageReceivedEvent): Mono<*> {
        return shikimori.animeSearch(AnimeSearch(args))
                .flatMap { event.channel.sendMessage(buildUrl(it)).asMono() }
    }

    @BotCommand(["on", "ongoings"], description = "find top 10 anime of current season")
    fun ongoings(args: String, event: MessageReceivedEvent): Mono<*> {
        val localDate = LocalDate.now()
        val year = localDate.year
        val season = fromLocalDate(localDate)

        val search = AnimeSearch(season = "${season.shikiSeason}_$year")
        val ongoings = shikimori.animeSearch(search, Page(1, 10))
        return ongoings
                .map { it.fold("") { acc, anime -> "${acc}\n${anime.russian} rating: ${anime.score} ep:${anime.episodes}" } }
                .flatMap { event.channel.sendMessage(it).asMono() }
    }

    @BotCommand(["bring"], visible = false)
    fun coffeeCommand(args: String, event: MessageReceivedEvent): Mono<*> {
        return if (args == "me coffee, please") {
            event.channel.sendMessage("Your coffee :coffee:").asMono()
        } else {
            Mono.empty<String>()
        }
    }

    private fun buildUrl(it: List<Anime>) = shikimoriApi + it[0].url
}
