package com.nikosar.animeforever.discord.services.repository

import com.nikosar.animeforever.discord.services.entity.Subscription
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface SubscriptionRepository : ReactiveCrudRepository<Subscription, Long> {
    @Query(
        """
        select s.*
        from subscription s
        join ANIME A on A.PROVIDER_ID = s.ANIME_ID
        where a.next_episode <= :currentTime"""
    )
    fun newReleasesFrom(currentTime: LocalDateTime): Flux<Subscription>

    @Query(
        """
        select s.*
        from subscription s
        where s.anime_id = :animeId and s.channel_id = :channelId and s.user_id = :userId
    """
    )
    fun findSubscription(animeId: Long, channelId: Long, userId: Long): Mono<Subscription>

    fun deleteAllByUserIdAndAnimeId(userId: Long, animeId: Long): Mono<Void>
}
