package com.nikosar.animeforever.discord

import com.nikosar.animeforever.shikimori.AnimeSearch
import com.nikosar.animeforever.shikimori.ShikimoriService
import net.dv8tion.jda.api.entities.ChannelType.TEXT
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MessageListener @Autowired constructor(
        val shikimoriService: ShikimoriService,
        @Value("\${shikimori.api}") val shikimori: String
) : ListenerAdapter(), Listener {
    private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (isListenedChannel(event)) {
            logger.info("Message from {} detected: {}", event.author, event.message)
            val textChannel = event.textChannel
            val text = event.message.contentRaw
            if (text.length > 3 && text.startsWith("!f")) {
                val arg = text.substring(3, text.length)
                val animeSearch = shikimoriService.animeSearch(AnimeSearch(arg))
                if (animeSearch.isNotEmpty()) {
                    textChannel.sendMessage(shikimori + animeSearch[0].url).queue()
                }
            }
        }
    }

    private fun isListenedChannel(event: MessageReceivedEvent) =
            !event.author.isBot && event.isFromType(TEXT) && event.textChannel.name.contains("test")
}