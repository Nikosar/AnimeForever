package com.nikosar.animeforever.shikimori

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ShikimoriServiceImpl
@Autowired constructor(
        private val restTemplate: RestTemplate,
        @Value("\${shikimori.api}")
        private val shikimori: String,
        @Value("\${shikimori.api.animes}")
        private val animes: String
) : ShikimoriService {
    private val animeListType = object : ParameterizedTypeReference<List<Anime>>() {}

    override fun animeSearch(search: AnimeSearch, page: Page): List<Anime> {
        val uri = URIBuilder(shikimori)
                .setPath(animes)
                .addParameters(search.toNameValuePairs())
                .addParameters(page.toNameValuePairs()).build()
        return restTemplate.exchange(uri, GET, null, animeListType).body!!
    }

}
