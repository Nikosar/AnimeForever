package com.nikosar.animeforever.shikimori

interface Shikimori {
    fun animeSearch(search: AnimeSearch, page: Page = Page(1, 1)): List<Anime>

    fun ongoings(): List<Anime>
}