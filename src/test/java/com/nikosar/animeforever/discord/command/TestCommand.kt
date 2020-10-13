package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Mono

@BotCommander
class TestCommand {
    @BotCommand(["!mockf", "!mockfind"], description = BRAVE_TEST_PHRASE)
    fun execute(args: String, event: MessageReceivedEvent): Mono<*> = Mono.just(args)
}

@BotCommander
class TestCommand2 {
    @BotCommand(["!testcommand"])
    fun execute(args: String, event: MessageReceivedEvent): Mono<*> = Mono.just("test")
}

const val BRAVE_TEST_PHRASE = "mock your life!"