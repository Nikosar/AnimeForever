package com.nikosar.animeforever.discord

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.shikimori.AnimeSearch
import com.nikosar.animeforever.shikimori.Page
import com.nikosar.animeforever.shikimori.Shikimori
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Command(
        private val shikimori: Shikimori,
        @Value("\${shikimori.api}")
        private val shikimoriApi: String
) {
    fun findAnime(args: List<String>, event: MessageReceivedEvent): Mono<*> {
        return shikimori.animeSearch(AnimeSearch(args[1]))
                .flatMap { event.channel.sendMessage(shikimoriApi + it[0].url).asMono() }
    }

    fun ongoings(args: List<String>, event: MessageReceivedEvent): Mono<*> {
        val ongoings = shikimori.animeSearch(AnimeSearch(season = "summer_2020"), Page(1, 10))
        return ongoings
                .map { it.fold("") { acc, anime -> "${acc}\n${anime.russian} rating: ${anime.score} ep:${anime.episodes}" } }
                .flatMap { event.channel.sendMessage(it).asMono() }
    }
}