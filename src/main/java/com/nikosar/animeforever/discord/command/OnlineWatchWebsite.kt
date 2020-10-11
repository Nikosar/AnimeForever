package com.nikosar.animeforever.discord.command

interface OnlineWatchWebsite {
    fun makeUrlFrom(search: String): String
}