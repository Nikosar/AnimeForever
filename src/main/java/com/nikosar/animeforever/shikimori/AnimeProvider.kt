package com.nikosar.animeforever.shikimori

import reactor.core.publisher.Mono

interface AnimeProvider : UrlMaker {
    fun findById(id: Long): Mono<Anime>

    fun search(search: AnimeSearch, page: Page = Page(1, 1)): Mono<List<Anime>>

    override fun makeUrlFrom(relative: String?): String?
}