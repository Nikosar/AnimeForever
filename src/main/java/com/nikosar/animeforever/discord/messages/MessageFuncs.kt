package com.nikosar.animeforever.discord.messages

import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import com.nikosar.animeforever.discord.commands.WATCH
import com.nikosar.animeforever.discord.util.asLink


fun watchLinks(
    watchSites: Map<String, OnlineWatchWebsite>,
    search: String
) = watchSites.entries
    .joinToString("\n") { asLink("$WATCH -> ${it.key}", it.value.makeUrlFrom(search)) }