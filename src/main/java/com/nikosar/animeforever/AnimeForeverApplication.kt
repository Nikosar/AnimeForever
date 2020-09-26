package com.nikosar.animeforever

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.http.impl.client.HttpClients
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

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
}

fun main() {
    SpringApplicationBuilder(AnimeForeverApplication::class.java).web(NONE).run()
}