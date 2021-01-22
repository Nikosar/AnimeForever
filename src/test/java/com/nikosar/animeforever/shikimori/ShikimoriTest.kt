package com.nikosar.animeforever.shikimori

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class ShikimoriTest
@Autowired constructor(
    private val shikimoriImpl: Shikimori
) : AnimeForeverApplicationTests() {

    @Test
    fun animeSearch() {
        StepVerifier.create(shikimoriImpl.findById(5114))
            .assertNext { assertTrue(it.genres?.isNotEmpty() ?: false) }
            .verifyComplete()
    }
}