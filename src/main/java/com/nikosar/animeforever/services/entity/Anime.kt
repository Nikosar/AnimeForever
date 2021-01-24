package com.nikosar.animeforever.services.entity

import com.nikosar.animeforever.shikimori.Anime
import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.time.ZoneId

data class Anime(
    @Id
    var id: Long?,
    val providerId: Long,
    val noticedEpisode: Int?,
    val nextEpisode: LocalDateTime?
) {
    constructor(anime: Anime) : this(
        null,
        anime.id,
        anime.episodesAired,
        anime.nextEpisodeAt
            ?.withZoneSameInstant(ZoneId.systemDefault())
            ?.toLocalDateTime()
    )
}