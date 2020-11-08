package com.nikosar.animeforever.discord.command.processor

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.CorePublisher
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

@Component
class CommandFactory(
        private val applicationContext: ApplicationContext,
        private val sequentialParams: SequentialParams

) {
    private lateinit var nameToCommand: Map<String, Command>

    fun createCommand(name: String): Command? {
        return nameToCommand[name.toLowerCase()]
    }

    @EventListener(ApplicationStartedEvent::class)
    private fun createMultiKeyMap() {
        val commandBeans = applicationContext.getBeansWithAnnotation(BotCommander::class.java)
        nameToCommand = commandBeans.asSequence()
                .map { it.value }
                .flatMap { command -> pairNameWith(command) }
                .associate { it }
    }

    private fun pairNameWith(command: Any) =
            command::class.memberFunctions.asSequence()
                    .filter { it.hasAnnotation<BotCommand>() }
                    .flatMap { kFunction -> nameCommandPair(kFunction, command) }

    private fun nameCommandPair(kFunction: KFunction<*>, command: Any): Sequence<Pair<String, Command>> {
        return kFunction.findAnnotation<BotCommand>()!!.value
                .asSequence()
                .map { Pair(it, command(kFunction, command)) }
    }

    private fun command(kFunction: KFunction<*>, command: Any): Command {
        return Command { args, event ->
            val funcParams = kFunction.parameters
            val paramsMap = mutableMapOf<KParameter, Any>()
            paramsMap[funcParams[0]] = command
            paramsMap[funcParams[1]] = event
            if (kFunction.hasAnnotation<Sequential>()) {
                paramsMap.putAll(sequentialParams.convert(args, funcParams))
            } else if (args.isNotBlank()) {
                paramsMap[funcParams[2]] = args
            }
            kFunction.callBy(paramsMap) as CorePublisher<*>
        }
    }
}