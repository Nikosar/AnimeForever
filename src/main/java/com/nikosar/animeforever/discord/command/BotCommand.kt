package com.nikosar.animeforever.discord.command

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class BotCommand(val value: Array<String>)