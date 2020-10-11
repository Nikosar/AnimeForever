package com.nikosar.animeforever.discord

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import com.nikosar.animeforever.discord.command.processor.CommandFactory
import com.nikosar.animeforever.discord.command.processor.CommandNotFoundException
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus.ONLINE
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.entities.ChannelType.PRIVATE
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
        private val commandFactory: CommandFactory
) : DiscordBot {
    private val logger: Logger = LoggerFactory.getLogger(JDABot::class.java)

    @EventListener(ApplicationReadyEvent::class)
    override fun start() {
        val jda = JDABuilder.createDefault(token)
                .setEventManager(ReactiveEventManager())
                .setActivity(Activity.listening("-help"))
                .setStatus(ONLINE)
//                .enableCache(VOICE_STATE)
                .build()

        jda.on<ReadyEvent>()
                .flatMap { join(jda.getGuildById(587261718602842115)!!) }
                .subscribe()
        jda.on<MessageReceivedEvent>()
                .filter { !it.author.isBot }
                .filter { it.message.channelType == PRIVATE || it.channel.name == "bot" }
                .flatMap { handleMessage(it) }
                .subscribe()
    }

    private fun handleMessage(event: MessageReceivedEvent): Mono<*> {
        return try {
            val allArgs = event.message.contentRaw.split(Pattern.compile(" "), 2)
            val command = allArgs[0]
            val args = if (allArgs.size > 1) allArgs[1] else ""
            commandFactory.createCommand(command)
                    .execute(args, event)
        } catch (e: CommandNotFoundException) {
            logger.error("{}. Message from {} detected: {}", e.message, event.author, event.message)
            Mono.empty<Any>()
        } catch (e: Exception) {
            logger.error("smth went wrong", e)
            Mono.empty<Any>()
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