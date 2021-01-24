package com.nikosar.animeforever.services

import com.nikosar.animeforever.services.entity.Anime
import com.nikosar.animeforever.services.repository.AnimeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnimeService(private val animeRepository: AnimeRepository) {

    fun save(anime: com.nikosar.animeforever.shikimori.Anime): Mono<Anime> {
        val newAnime = Anime(anime)
        return animeRepository.findByProviderId(anime.id)
            .flatMap { animeRepository.save(newAnime.apply { id = it.id }) }
            .switchIfEmpty(animeRepository.save(newAnime))
    }

    fun saveAll(entities: Iterable<com.nikosar.animeforever.shikimori.Anime>): Flux<Anime> {
        return animeRepository.saveAll(entities.map { Anime(it) })
    }

    fun saveIfNotExist(anime: com.nikosar.animeforever.shikimori.Anime): Mono<Anime> {
        return animeRepository.findByProviderId(anime.id)
            .switchIfEmpty(animeRepository.save(Anime(anime)))
    }

    fun findByProviderId(id: Long): Mono<Anime> {
        return animeRepository.findByProviderId(id)
    }
}