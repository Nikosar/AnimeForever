package com.nikosar.animeforever.discord.command

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AnimeGo(
        @Value("\${animego.url}")
        private val onlineWatchUrl: String
) : OnlineWatchWebsite {
    override fun makeUrlFrom(search: String) = URIBuilder(onlineWatchUrl)
            .addParameter("q", search).build().toString()
}