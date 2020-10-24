package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@BotCommander
class AnimeSearchCommand(
        private val animeProvider: AnimeProvider,
        private val onlineWatchWebsite: OnlineWatchWebsite
) {
    @BotCommand(value = ["-f", "find"], description = "find anime with max rating by query")
    fun findAnime(args: String, event: MessageReceivedEvent): Flux<*> =
            animeProvider.search(AnimeSearch(args))
                    .filter { it.isNotEmpty() }
                    .map { it[0] }
                    .flatMap { animeProvider.findById(it.id) }
                    .flatMapMany {
                        Flux.just(
                                createFindMessage(it),
                                createWatchMessage(it)
                        )
                    }
                    .defaultIfEmpty(MessageBuilder(CRAB).build())
                    .flatMap { event.channel.sendMessage(it).asMono() }

    private fun createFindMessage(anime: Anime): Message {
        val title = anime.russian
        val url = animeProvider.makeUrlFrom(anime.url)
        val imageUrl = animeProvider.makeUrlFrom(anime.image?.original)
        val messageBuilder = MessageBuilder().setContent(url)
        anime.apply {
            val embed = EmbedBuilder().setColor(BEST_PINK_COLOR)
                    .setTitle(title, url)
                    .setImage(imageUrl)
                    .setDescription(anime.description)
                    .addField(RATING, "$score$RATING_EMOJI", true)
                    .addField(EPISODES, "$episodesAired/$episodes", true)
                    .addField(GENRES, genres?.joinToString { it.russian }, false)
                    .build()
            messageBuilder.setEmbed(embed)
        }
        return messageBuilder.build()
    }

    private fun createWatchMessage(anime: Anime): Message {
        val title = "$WATCH -> ${anime.russian}"
        val url = onlineWatchWebsite.makeUrlFrom(anime.name)
        val embed = EmbedBuilder().setColor(BEST_PINK_COLOR)
                .setTitle(title, url)
                .build()
        return MessageBuilder().setEmbed(embed).build()
    }

    @BotCommand(["on", "ongoings"], description = "find top 10 anime of current season")
    fun ongoings(args: String, event: MessageReceivedEvent): Mono<*> = requestOngoings()
            .map {
                val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR).setTitle(ONGOINGS)
                it.forEachIndexed { i, anime ->
                    anime.apply {
                        embedBuilder.addField("${i + 1}. $russian",
                                "$score$RATING_EMOJI  ep:$episodesAired/$episodes",
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
}

const val BEST_PINK_COLOR = 16712698
const val WATCH = "Смотреть"
const val ONGOINGS = "Онгоинги"
const val RATING = "Рейтинг"
const val EPISODES = "Эпизоды"
const val GENRES = "Жанры"
const val RATING_EMOJI = ":star"
const val CRAB = ":crab:"