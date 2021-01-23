package com.nikosar.animeforever.discord.command.utils

import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.asLink
import com.nikosar.animeforever.discord.command.*
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.AnimeProvider
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Component

@Component
class SearchMessageCreator(
    private val animeProvider: AnimeProvider,
    private val watchSites: Map<String, OnlineWatchWebsite>
) {
    fun createFindMessage(anime: Anime): Message {
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

    fun animeListMessage(animes: List<Anime>, page: Int, size: Int): MessageEmbed {
        val embedBuilder = EmbedBuilder().setColor(BEST_PINK_COLOR).setTitle(ONGOINGS)
        animes.forEachIndexed { i, anime ->
            val startIndex = (page - 1) * size + 1
            anime.apply {
                embedBuilder.addField(
                    "${i + startIndex}. $russian",
                    "$score$RATING_EMOJI  ep:$episodesAired/$episodes",
                    false
                )
            }
        }
        return embedBuilder.build()
    }

    fun createWatchMessage(anime: Anime): Message =
        watchMessageBuilder(watchSites, anime).build()

}

fun newEpisodeIsOut(
    userMentions: String,
    anime: Anime,
    watchSites: Map<String, OnlineWatchWebsite>
): Message =
    watchMessageBuilder(watchSites, anime).setContent("$userMentions\nNew episode ${anime.episodesAired}").build()

private fun watchMessageBuilder(
    watchSites: Map<String, OnlineWatchWebsite>,
    anime: Anime
): MessageBuilder {
    val description = watchSites.entries
        .joinToString("\n") { asLink("$WATCH -> ${it.key}", it.value.makeUrlFrom(anime.name)) }
    val embed = EmbedBuilder().setColor(BEST_PINK_COLOR)
        .setDescription(description)
        .setTitle(anime.russian)
        .build()
    return MessageBuilder().setEmbed(embed)
}


fun subscribeSuccessful(user: User, anime: Anime): Message = MessageBuilder()
    .setContent("${user.asMention} successfully subscribed to ${anime.name}")
    .build()

fun alreadyAired(user: User, anime: Anime): Message = MessageBuilder()
    .setContent("${user.asMention} ${anime.name} is already out")
    .build()

fun subscribePrivateChannel(user: User): Message = MessageBuilder()
    .setContent("${user.asMention} can't subscribe you in private channel, use guild")
    .build()

fun alreadySubscribed(user: User, anime: Anime): Message = MessageBuilder()
    .setContent("${user.asMention} you already subscribed to ${anime.name}")
    .build()

fun unsubscribedSuccessfully(user: User, anime: Anime): Message = MessageBuilder()
    .setContent("${user.asMention} you unsubscribed from ${anime.name}")
    .build()

fun alreadyUnsubscribed(user: User, anime: Anime): Message = MessageBuilder()
    .setContent("${user.asMention} not subscribed to ${anime.name}")
    .build()