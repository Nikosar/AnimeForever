package com.nikosar.animeforever

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.nikosar.animeforever.animesites.OnlineWatchWebsite
import org.apache.http.impl.client.HttpClients
import org.modelmapper.ModelMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@SpringBootApplication
@PropertySources(
        PropertySource("classpath:/application.properties"),
        PropertySource("classpath:/secret.properties"),
)
open class AnimeForeverApplication {
    @Bean
    open fun restTemplate(): RestTemplate {
        val default = HttpClients.createDefault()
        val requestFactory = HttpComponentsClientHttpRequestFactory(default)
        return RestTemplate(requestFactory)
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper.registerKotlinModule()
    }

    @Bean
    open fun webClient(): WebClient {
        val httpClient = HttpClient.create().secure()
        return WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(httpClient)).build()
    }

    @Bean
    open fun modelMapper(): ModelMapper = ModelMapper()

    @Bean
    open fun watchSites(applicationContext: ApplicationContext): Map<String, OnlineWatchWebsite> {
        return applicationContext.getBeansOfType(OnlineWatchWebsite::class.java)
    }
}

fun main() {
    SpringApplication.run(AnimeForeverApplication::class.java)
}