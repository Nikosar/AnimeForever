package com.nikosar.animeforever.shikimori

import reactor.core.publisher.Mono

interface AnimeProvider {
    fun findById(id: Long): Mono<Anime>

    fun search(search: AnimeSearch, page: Page = Page(1, 1)): Mono<List<Anime>>

    fun ongoings(page: Page = Page(1, 10)): Mono<List<Anime>>

    fun makeUrlFrom(animeUrl: String?): String?
}