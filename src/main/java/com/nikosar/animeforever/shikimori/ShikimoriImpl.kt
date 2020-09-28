package com.nikosar.animeforever.shikimori

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class ShikimoriImpl(
        private val webClient: WebClient,
        @Value("\${shikimori.api}")
        private val shikimori: String,
        @Value("\${shikimori.api.animes}")
        private val animes: String
) : Shikimori {
    private val animeListType = object : ParameterizedTypeReference<List<Anime>>() {}

    override fun animeSearch(search: AnimeSearch, page: Page): Mono<List<Anime>> {
        val uri = URIBuilder(shikimori)
                .setPath(animes)
                .addParameters(search.toNameValuePairs())
                .addParameters(page.toNameValuePairs()).build()
        return webClient.get().uri(uri)
                .accept(APPLICATION_JSON)
                .retrieve().bodyToMono(animeListType)
    }

    override fun ongoings(): Mono<List<Anime>> = animeSearch(AnimeSearch(season = "summer_2020"), Page(1, 10))


}