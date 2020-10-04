package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.discord.command.processor.BotCommand
import com.nikosar.animeforever.discord.command.processor.BotCommander
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

@BotCommander
class HelpCommand(val applicationContext: ApplicationContext) {
    @BotCommand(["!help"], visible = false)
    fun helpMePlease(args: String, event: MessageReceivedEvent): String {
        val commanders = applicationContext.getBeansWithAnnotation<BotCommander>()
        return commanders.values.asSequence()
                .map { it::class }
                .flatMap { it.memberFunctions }
                .filter { it.hasAnnotation<BotCommand>() }
                .map { it.findAnnotation<BotCommand>()!! }
                .filter { it.visible }
                .map { "${it.value.asList()} ${it.description}" }
                .joinToString("\n")
    }
}