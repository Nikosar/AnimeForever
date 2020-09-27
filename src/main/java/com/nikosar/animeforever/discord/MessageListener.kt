package com.nikosar.animeforever.discord

import com.nikosar.animeforever.shikimori.AnimeSearch
import com.nikosar.animeforever.shikimori.Page
import com.nikosar.animeforever.shikimori.Shikimori
import net.dv8tion.jda.api.entities.ChannelType.TEXT
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MessageListener(
        private val shikimori: Shikimori,
        @Value("\${shikimori.api}") private val shikimoriUrl: String
) : ListenerAdapter(), Listener {
    private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (isListened(event)) {
            logger.info("Message from {} detected: {}", event.author, event.message)
            val textChannel = event.textChannel
            val text = event.message.contentRaw
            if (text.length > 3 && text.startsWith("!f")) {
                val arg = text.substring(3, text.length)
                val animeSearch = shikimori.animeSearch(AnimeSearch(arg))
                if (animeSearch.isNotEmpty()) {
                    textChannel.sendMessage(shikimoriUrl + animeSearch[0].url).queue()
                }
            }
            if (text.startsWith("ongoings")) {
                val ongoings = shikimori.animeSearch(AnimeSearch(season = "summer_2020"), Page(1, 10))
                val fold = ongoings.fold("") { acc, anime -> "${acc}\n${anime.russian} rating: ${anime.score} ep:${anime.episodes}" }
                textChannel.sendMessage(fold).queue()
            }
        }
    }

    private fun isListened(event: MessageReceivedEvent) =
            !event.author.isBot && event.isFromType(TEXT) && event.textChannel.name.contains("test")
}