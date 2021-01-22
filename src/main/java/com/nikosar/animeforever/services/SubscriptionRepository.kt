package com.nikosar.animeforever.services

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface SubscriptionRepository : ReactiveCrudRepository<Subscription, Long> {
    @Query(
        """
        select s.*
        from subscription s
        join ANIME A on A.PROVIDER_ID = s.ANIME_ID
        where a.next_episode <= :currentTime"""
    )
    fun newReleases(currentTime: LocalDateTime): Flux<Subscription>
}
