package com.nikosar.animeforever.discord.command.processor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class BotCommand(
        val value: Array<String>,
        val description: String = "",
        val visible: Boolean = true
)