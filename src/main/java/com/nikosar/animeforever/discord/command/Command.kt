package com.nikosar.animeforever.discord.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Mono

fun interface Command {
    fun execute(args: String, event: MessageReceivedEvent): Mono<*>
}