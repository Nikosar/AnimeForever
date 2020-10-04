package com.nikosar.animeforever.discord.command.processor

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

@Component
class CommandFactory(private val applicationContext: ApplicationContext) {
    private lateinit var nameToCommand: Map<String, Command>

    fun createCommand(name: String): Command {
        return nameToCommand[name] ?: throw CommandNotFoundException("command $name not found")
    }

    @EventListener(ApplicationStartedEvent::class)
    private fun createMultiKeyMap() {
        val commandBeans = applicationContext.getBeansWithAnnotation(BotCommander::class.java)
        nameToCommand = commandBeans.asSequence()
                .map { it.value }
                .flatMap { command -> pairCommandNameWithCommand(command) }
                .associate { it }
    }

    private fun pairCommandNameWithCommand(command: Any) =
            command::class.memberFunctions.asSequence()
                    .filter { it.hasAnnotation<BotCommand>() }
                    .flatMap { kFunction -> functionToNameCommandPair(kFunction, command) }

    private fun functionToNameCommandPair(kFunction: KFunction<*>, command: Any): Sequence<Pair<String, Command>> {
        return kFunction.findAnnotation<BotCommand>()!!.value
                .asSequence()
                .map { Pair(it, command(kFunction, command)) }
    }

    private fun command(kFunction: KFunction<*>, command: Any): Command {
        return Command { args, event -> kFunction.call(command, args, event) as Mono<*> }
    }
}