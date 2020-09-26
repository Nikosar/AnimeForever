package com.nikosar.animeforever.shikimori

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Anime(
        var id: Long,
        var name: String,
        var russian: String,
        var url: String,
        var kind: String?,
        var score: BigDecimal = BigDecimal.ZERO,
        var status: String?,
        var episodes: Int,
        var episodesAired: Int,
        var airedOn: LocalDate?,
        var releasedOn: LocalDate?,
        var rating: String?,
        var licenseNameRu: String?,
        var duration: Int,
        var description: String?,
        var descriptionSource: String?,
        var franchise: String?,
        var favoured: Boolean,
        var anons: Boolean,
        var ongoing: Boolean,
        var threadId: Long,
        var topicId: Long,
        var myanimelistId: Long,
        var updatedAt: LocalDateTime?,
        var nextEpisodeAt: LocalDateTime?,
)