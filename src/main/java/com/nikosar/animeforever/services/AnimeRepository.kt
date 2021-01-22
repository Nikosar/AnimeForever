package com.nikosar.animeforever.services

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface AnimeRepository : ReactiveCrudRepository<Anime, Long> {
    fun findByProviderId(providerId: Long): Mono<Anime>
}