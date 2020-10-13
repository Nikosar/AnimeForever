package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.discord.AudioHandler
import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

@BotCommander
class Mzhe {
    private val logger: Logger = LoggerFactory.getLogger(Mzhe::class.java)

    @BotCommand(["join"])
    fun join(args: String, messageReceivedEvent: MessageReceivedEvent): Mono<Any> {
        val voiceState = messageReceivedEvent.member?.voiceState
        if (voiceState?.inVoiceChannel() == true) {
            val channel = voiceState.channel!!
            val guild = voiceState.guild
            val audioManager = guild.audioManager
            val audioHandler = AudioHandler()
            audioManager.receivingHandler = audioHandler
            audioManager.openAudioConnection(channel)
        }
        return Mono.empty()
    }

    @BotCommand(["leave"])
    fun leave(args: String, messageReceivedEvent: MessageReceivedEvent): Mono<Any> {
        val audioManager = messageReceivedEvent.guild.audioManager
        if (audioManager.isConnected) {
            val audioHandler = audioManager.receivingHandler as AudioHandler
            logger.info("Session sound level was {}", audioHandler.sessionLevel)
            audioManager.closeAudioConnection()
        }
        return Mono.empty()
    }
}