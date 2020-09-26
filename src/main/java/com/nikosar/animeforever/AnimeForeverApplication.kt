package com.nikosar.animeforever

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.http.impl.client.HttpClients
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@SpringBootApplication
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
}

fun main(vararg args: String) {
    SpringApplication.run(AnimeForeverApplication::class.java)
}