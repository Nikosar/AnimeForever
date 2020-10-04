package com.nikosar.animeforever.discord

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.BotCommand
import com.nikosar.animeforever.discord.command.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Mono
import java.time.LocalDate

@BotCommander
class Command(
        private val shikimori: Shikimori,
        @Value("\${shikimori.api}")
        private val shikimoriApi: String
) {
    @BotCommand(["!f"])
    fun findAnime(args: String, event: MessageReceivedEvent): Mono<*> {
        return shikimori.animeSearch(AnimeSearch(args))
                .flatMap { event.channel.sendMessage(buildUrl(it)).asMono() }
    }

    @BotCommand(["ongoings"])
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

    @BotCommand(["bring"])
    fun command(args: String, event: MessageReceivedEvent): Mono<*> {
        return event.channel.sendMessage("your coffee!").asMono()
    }

    private fun buildUrl(it: List<Anime>) = shikimoriApi + it[0].url
}