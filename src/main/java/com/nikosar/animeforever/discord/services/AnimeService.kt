package com.nikosar.animeforever.discord.services

import com.nikosar.animeforever.discord.services.entity.Anime
import com.nikosar.animeforever.discord.services.repository.AnimeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
open class AnimeService(private val animeRepository: AnimeRepository) {

    @Transactional
    open fun save(anime: com.nikosar.animeforever.shikimori.Anime): Mono<Anime> {
        val newAnime = Anime(anime)
        return animeRepository.findByProviderId(anime.id)
            .flatMap { animeRepository.save(newAnime.apply { id = it.id }) }
            .switchIfEmpty(animeRepository.save(newAnime))
    }

    @Transactional
    open fun saveAll(entities: Iterable<com.nikosar.animeforever.shikimori.Anime>): Flux<Anime> {
        return animeRepository.saveAll(entities.map { Anime(it) })
    }

    @Transactional
    open fun saveIfNotExist(anime: com.nikosar.animeforever.shikimori.Anime): Mono<Anime> {
        return animeRepository.findByProviderId(anime.id)
            .switchIfEmpty(animeRepository.save(Anime(anime)))
    }

    open fun findByProviderId(id: Long): Mono<Anime> {
        return animeRepository.findByProviderId(id)
    }
}