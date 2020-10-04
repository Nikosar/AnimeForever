package com.nikosar.animeforever.discord.command.processor

import net.dv8tion.jda.api.events.Event
import reactor.core.publisher.Mono

fun interface Command {
    fun execute(args: String, event: Event): Mono<*>
}