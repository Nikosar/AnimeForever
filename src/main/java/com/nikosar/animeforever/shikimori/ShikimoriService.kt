package com.nikosar.animeforever.shikimori

interface ShikimoriService {
    fun animeSearch(search: AnimeSearch, page: Page = Page(1, 1)): List<Anime>
}