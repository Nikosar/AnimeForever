package com.nikosar.animeforever.discord.services.repository

import com.nikosar.animeforever.discord.services.entity.Anime
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AnimeRepository : ReactiveCrudRepository<Anime, Long> {
    fun findByProviderId(providerId: Long): Mono<Anime>
}