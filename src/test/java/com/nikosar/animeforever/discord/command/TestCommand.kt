package com.nikosar.animeforever.discord.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Mono

@BotCommander
class TestCommand {
    @BotCommand(["!mockf", "!mockfind"])
    fun execute(args: String, event: MessageReceivedEvent): Mono<*> = Mono.just(args)
}

@BotCommander
class TestCommand2 {
    @BotCommand(["!testCommand"])
    fun execute(args: String, event: MessageReceivedEvent): Mono<*> = Mono.just("test")
}