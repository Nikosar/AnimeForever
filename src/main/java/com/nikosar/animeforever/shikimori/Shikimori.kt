package com.nikosar.animeforever.shikimori

import reactor.core.publisher.Mono

interface Shikimori {
    fun animeSearch(search: AnimeSearch, page: Page = Page(1, 1)): Mono<List<Anime>>

    fun ongoings(): Mono<List<Anime>>
}