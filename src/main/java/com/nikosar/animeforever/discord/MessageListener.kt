package com.nikosar.animeforever.discord

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageListener : ListenerAdapter(), Listener {
    private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message
        val author = event.author
        if (!author.isBot) {
            if (event.isFromType(ChannelType.TEXT)) {
                val textChannel = event.textChannel
                val guild = event.guild
                logger.info("{} {} {} {}", message, author, guild, textChannel)
                if (textChannel.name.contains("test")) {
                    textChannel.sendMessage("Hello, world").queue()
                }
            }
        }
    }
}