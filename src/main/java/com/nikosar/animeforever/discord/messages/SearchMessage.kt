package com.nikosar.animeforever.discord.messages

import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.commands.*
import com.nikosar.animeforever.shikimori.Anime
import com.nikosar.animeforever.shikimori.UrlMaker
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

fun createFindMessage(anime: Anime, urlMaker: UrlMaker): Message {
    val title = anime.russian
    val url = urlMaker.makeUrlFrom(anime.url)
    val imageUrl = urlMaker.makeUrlFrom(anime.image?.original)
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

fun createWatchMessage(anime: Anime, watchSites: Map<String, OnlineWatchWebsite>): Message {
    val description = watchLinks(watchSites, anime.name)
    val embed = EmbedBuilder().setColor(BEST_PINK_COLOR)
        .setDescription(description)
        .setTitle(anime.russian)
        .build()
    return MessageBuilder().setEmbed(embed).build()
}