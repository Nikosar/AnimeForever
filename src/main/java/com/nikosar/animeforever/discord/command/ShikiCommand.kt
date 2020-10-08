package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import reactor.core.publisher.Mono
import java.time.LocalDate

@BotCommander
class ShikiCommand(
        private val shikimori: Shikimori,
        @Value("\${shikimori.api}")
        private val shikimoriApi: String,
        private val onlineWatchWebsite: OnlineWatchWebsite
) {
    @BotCommand(value = ["-f", "find"], description = "find anime with max rating by query")
    fun findAnime(args: String, event: MessageReceivedEvent): Mono<*> {
        return shikimori.animeSearch(AnimeSearch(args))
                .map { if (it.isEmpty()) ":crab:" else buildUrl(it[0]) }
                .flatMap { event.channel.sendMessage(it).asMono() }
    }

    @BotCommand(value = ["-w", "watch"], description = "prepare search to animego")
    fun watchAnime(args: String, event: MessageReceivedEvent): Mono<*> {
        return shikimori.animeSearch(AnimeSearch(args))
                .map {
                    if (it.isNotEmpty()) {
                        val anime = it[0]
                        val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR)
                                .setTitle(anime.russian, onlineWatchWebsite.makeUrlFrom(anime.name))
                        anime.image?.x48?.let { x48 -> embedBuilder.setThumbnail(shikimoriApi + x48) }
                        embedBuilder.build()
                    } else {
                        EmbedBuilder().setTitle(":crab:").build()
                    }
                }
                .flatMap { event.channel.sendMessage(it).asMono() }
    }

    private fun watchUrl(it: List<Anime>) = if (it.isEmpty()) ":crab:" else onlineWatchWebsite.makeUrlFrom(it[0].russian)

    @BotCommand(["on", "ongoings"], description = "find top 10 anime of current season")
    fun ongoings(args: String, event: MessageReceivedEvent): Mono<*> = requestOngoings()
            .map {
                val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR).setTitle("Онгоинги")
                it.forEachIndexed { i, anime ->
                    anime.apply {
                        embedBuilder.addField("${i + 1}. $russian",
                                "$score:star:  ep:$episodesAired/$episodes  $",
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
        return shikimori.animeSearch(search, Page(1, 10))
    }

    @BotCommand(["bring"], visible = false)
    fun coffeeCommand(args: String, event: MessageReceivedEvent): Mono<*> {
        return if (args == "me coffee, please") {
            event.channel.sendMessage("Your coffee :coffee:").asMono()
        } else {
            Mono.empty<String>()
        }
    }

    private fun buildUrl(it: Anime) = shikimoriApi + it.url
}

const val BEST_PINK_COLOR = 16712698