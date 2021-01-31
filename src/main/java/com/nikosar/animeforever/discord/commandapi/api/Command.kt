package com.nikosar.animeforever.discord.commandapi.api

import net.dv8tion.jda.api.events.Event
import org.reactivestreams.Publisher

fun interface Command {
    fun execute(args: String, event: Event): Publisher<*>
}