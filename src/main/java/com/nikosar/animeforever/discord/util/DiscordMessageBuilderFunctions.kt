package com.nikosar.animeforever.discord.util

fun StringBuilder.appendLink(name: String, url: String) {
    append("[${name}](${url})")
}

fun asLink(name: String, url: String): String {
    return ("[${name}](${url})")
}