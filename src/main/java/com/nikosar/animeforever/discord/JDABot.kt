package com.nikosar.animeforever.discord

import net.dv8tion.jda.api.JDABuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JDABot @Autowired constructor(
        @Value("\${discord.bot.token}") val token: String,
        val listener: Listener
) : DiscordBot {
    private val logger: Logger = LoggerFactory.getLogger(JDABot::class.java)

    @EventListener(ApplicationReadyEvent::class)
    override fun start() {
        logger.info("strating jda bot")
        val builder = JDABuilder.createDefault(token)
                .addEventListeners(listener)
        builder.build()
    }
}