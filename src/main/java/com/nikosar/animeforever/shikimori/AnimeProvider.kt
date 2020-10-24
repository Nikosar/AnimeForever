package com.nikosar.animeforever.shikimori

import reactor.core.publisher.Mono

interface AnimeProvider {
    fun search(search: AnimeSearch, page: Page = Page(1, 1)): Mono<List<Anime>>

    fun ongoings(): Mono<List<Anime>>
}