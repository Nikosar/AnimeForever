package com.nikosar.animeforever.shikimori

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class ShikimoriNonBlockingTest @Autowired constructor(
        private val shikimoriNonBlocking: ShikimoriNonBlocking
) : AnimeForeverApplicationTests() {

    @Test
    fun animeSearch() {
        StepVerifier.create(shikimoriNonBlocking.ongoings())
                .assertNext { it.isNotEmpty() }
                .verifyComplete()
    }
}