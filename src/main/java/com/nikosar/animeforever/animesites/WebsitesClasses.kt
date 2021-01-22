package com.nikosar.animeforever.animesites

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

    override fun name(): String = "Anime Go"
}

@Service
class YummyAnime(
        @Value("\${yummyanime.url}")
        private val onlineWatchUrl: String
) : OnlineWatchWebsite {
    override fun makeUrlFrom(search: String) = URIBuilder(onlineWatchUrl)
        .addParameter("word", search).build().toString()

    override fun name(): String = "Yummyanime"
}