package com.nikosar.animeforever.services.entity

import com.nikosar.animeforever.shikimori.Anime
import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.time.ZoneId

data class Anime(
    @Id
    var id: Long?,
    var providerId: Long,
    var nextEpisode: LocalDateTime?
) {
    constructor(anime: Anime) : this(
        null,
        anime.id,
        anime.nextEpisodeAt
            ?.withZoneSameInstant(ZoneId.systemDefault())
            ?.toLocalDateTime()
    )
}