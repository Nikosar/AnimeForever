package com.nikosar.animeforever.services

import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Anime(
    @Id
    var id: Long?,
    var providerId: Long,
    var nextEpisode: LocalDateTime
)