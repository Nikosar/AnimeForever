package com.nikosar.animeforever.shikimori

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class Shikimori(
        private val webClient: WebClient,
        @Value("\${shikimori.api}")
        private val shikimori: String,
        @Value("\${shikimori.api.animes}")
        private val animes: String,
        @Value("spring.application.name")
        private val applicationName: String
) : AnimeProvider {
    private val animeListType = object : ParameterizedTypeReference<List<Anime>>() {}
    private val descriptionLinks = Regex("\\[.+?]")


    override fun findById(id: Long): Mono<Anime> {
        val uri = URIBuilder(shikimori)
                .setPath("$animes/$id").build()
        return webClient.get().uri(uri)
                .header(USER_AGENT, applicationName)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Anime::class.java)
                .map {
                    it.description = it.description?.replace(descriptionLinks, "")
                    it
                }
    }

    override fun search(search: AnimeSearch, page: Page): Mono<List<Anime>> {
        val uri = URIBuilder(shikimori)
                .setPath(animes)
                .addParameters(search.toNameValuePairs())
                .addParameters(page.toNameValuePairs()).build()
        return webClient.get().uri(uri)
                .accept(APPLICATION_JSON)
                .retrieve().bodyToMono(animeListType)
    }

    override fun makeUrlFrom(animeUrl: String?) = if (animeUrl != null) shikimori + animeUrl else null
}