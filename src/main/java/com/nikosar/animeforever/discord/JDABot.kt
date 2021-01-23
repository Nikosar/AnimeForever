package com.nikosar.animeforever.discord

import club.minnced.jda.reactor.on
import com.nikosar.animeforever.discord.command.processor.CommandFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.regex.Pattern

@Service
open class JDABot(
    private val jda: JDA,
    private val commandFactory: CommandFactory
) : DiscordBot {
    private val logger: Logger = LoggerFactory.getLogger(JDABot::class.java)

    @EventListener(ApplicationReadyEvent::class)
    override fun start() {
        jda.on<MessageReceivedEvent>()
                .filter { !it.author.isBot }
                .flatMap { handleMessage(it) }
                .doOnError { logger.error(it.message) }
                .subscribe()
    }

    private fun handleMessage(event: MessageReceivedEvent): Publisher<*> {
        return try {
            val messageRaw = event.message.contentRaw
            logger.debug("Received message from ${event.author.name}: $messageRaw")
            val allArgs = messageRaw.split(Pattern.compile(" "), 2)
            val command = allArgs[0]
            val args = if (allArgs.size > 1) allArgs[1] else ""
            commandFactory.createCommand(command)
                ?.execute(args, event) ?: Mono.empty<Any>()
        } catch (e: Exception) {
            logger.error("smth went wrong", e)
            Mono.empty<Any>()
        }
    }
}