package com.nikosar.animeforever.discord.command

import club.minnced.jda.reactor.asMono
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.shikimori.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.Year

@BotCommander
class AnimeSearchCommand(
        private val animeProvider: AnimeProvider,
        private val onlineWatchWebsite: OnlineWatchWebsite
) {
    @BotCommand(value = ["-f", "find"], description = "find anime with max rating by query")
    fun findAnime(event: MessageReceivedEvent, args: String): Flux<*> =
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
                    .setDescription(description)
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
    fun ongoings(event: MessageReceivedEvent, page: Int = 1, size: Int = 10): Mono<*> = best(event)

    @BotCommand(["best"], description = "best anime for given season or year")
    fun best(event: MessageReceivedEvent,
             year: Int = Year.now().value,
             season: Season = fromLocalDate(LocalDate.now()),
             page: Int = 1,
             size: Int = 10
    ): Mono<*> = animeProvider.search(AnimeSearch(season = season, year = year), Page(page, size))
            .map { listMessage(it, page, size) }
            .flatMap { event.channel.sendMessage(it).asMono() }

    private fun listMessage(animes: List<Anime>, page: Int, size: Int): MessageEmbed {
        val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR).setTitle(ONGOINGS)
        animes.forEachIndexed { i, anime ->
            val startIndex = (page - 1) * size + 1
            anime.apply {
                embedBuilder.addField("${i + startIndex}. $russian",
                        "$score$RATING_EMOJI  ep:$episodesAired/$episodes",
                        false)
            }
        }
        return embedBuilder.build()
    }

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