package com.nikosar.animeforever.animesites

interface OnlineWatchWebsite {
    fun makeUrlFrom(search: String): String

    fun name(): String
}