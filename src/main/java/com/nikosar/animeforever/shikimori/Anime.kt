package com.nikosar.animeforever.shikimori

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Anime(
        var id: Long,
        var name: String,
        var russian: String,
        var url: String,
        var image: ImageUrls?,
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
        var genres: List<Genre>?,
        var updatedAt: ZonedDateTime?,
        var nextEpisodeAt: ZonedDateTime?,
)