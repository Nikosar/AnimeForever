package com.nikosar.animeforever.discord

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus.ONLINE
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.regex.Pattern

@Service
open class JDABot(
        @Value("\${discord.bot.token}") val token: String,
        private val command: Command
) : DiscordBot {
    private val logger: Logger = LoggerFactory.getLogger(JDABot::class.java)

    @EventListener(ApplicationReadyEvent::class)
    override fun start() {
        logger.info("strating jda bot")
        val jda = JDABuilder.createDefault(token)
                .setEventManager(ReactiveEventManager())
                .setActivity(Activity.watching("за Коляном"))
                .setStatus(ONLINE)
//                .enableCache(VOICE_STATE)
                .build()

        jda.on<ReadyEvent>()
                .flatMap { join(jda.getGuildById(587261718602842115)!!) }
                .subscribe()
        jda.on<MessageReceivedEvent>()
                .filter { !it.author.isBot }
                .flatMap { handle(it) }
                .subscribe()
    }

    private fun handle(event: MessageReceivedEvent): Mono<*> {
        logger.info("Message from {} detected: {}", event.author, event.message)

        val args = event.message.contentRaw.split(Pattern.compile(" "))
        return when (args[0]) {
            "!f" -> command.findAnime(args, event)
            "ongoings" -> command.ongoings(args, event)
            else -> Mono.empty<String>()
        }
    }

    private fun join(guild: Guild): Mono<*> {
        val channel = guild.getVoiceChannelById(587261718602842127)

        //                val channel = voiceState?.channel
        //                val guild = channel?.guild
        val audioManager = guild?.audioManager
        val audioHandler = AudioHandler()
        audioManager.sendingHandler = audioHandler
        audioManager.receivingHandler = audioHandler
        logger.info("channel {}", channel)

        audioManager.openAudioConnection(channel)

        return Mono.empty<String>()
    }
}