package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import com.nikosar.animeforever.discord.command.processor.Sequential
import com.nikosar.animeforever.shikimori.Season
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.core.publisher.Mono

@BotCommander
class TestCommand {
    @BotCommand(["!mockf", "!mockfind"], description = BRAVE_TEST_PHRASE)
    fun execute(event: MessageReceivedEvent, args: String): Mono<*> = Mono.just(args)
}

@BotCommander
class TestCommand2 {
    @BotCommand(["!testcommand"])
    fun execute(event: MessageReceivedEvent): Mono<*> = Mono.just("test")
}

@BotCommander
class TestCommandWithoutArgs {
    @BotCommand(["!noargs"], description = BRAVE_TEST_PHRASE)
    fun execute(event: MessageReceivedEvent): Mono<*> = Mono.just("123")
}

@BotCommander
class TestCommandWithArgs {
    @BotCommand(["!sum"], description = BRAVE_TEST_PHRASE)
    @Sequential
    fun execute(event: MessageReceivedEvent, i: Int, long: Long, season: Season): Mono<*> = Mono.just("${i + long} $season")
}

const val BRAVE_TEST_PHRASE = "mock your life!"