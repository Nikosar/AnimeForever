package com.nikosar.animeforever

import com.nikosar.animeforever.discord.JDABot
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
internal open class AnimeForeverApplicationTests {
    @MockBean
    lateinit var jdaBot: JDABot

    @Test
    fun contextLoads() {
    }
}