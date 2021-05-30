package com.nikosar.animeforever.discord.messages

import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.commands.*
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.UrlMaker
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

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

fun newEpisodeIsOut(
    userMentions: String,
    anime: Anime,
    urlMaker: UrlMaker,
    watchSites: Map<String, OnlineWatchWebsite>
): Message {
    val embed = EmbedBuilder()
    anime.apply {
        val description = watchLinks(watchSites, name)
        val animeUrl = urlMaker.makeUrlFrom(anime.url)
        val imageUrl = urlMaker.makeUrlFrom(image?.original)
        embed.setColor(BEST_PINK_COLOR)
            .setDescription(description)
            .setTitle(anime.russian, animeUrl)
            .addField(RATING, "$score$RATING_EMOJI", true)
            .addField(EPISODES, "$episodesAired/$episodes", true)
            .addField(GENRES, genres?.joinToString { it.russian }, false)
            .setImage(imageUrl)
    }

    return MessageBuilder().setEmbed(embed.build())
        .setContent("$userMentions\nNew episode ${anime.episodesAired}")
        .build()
}