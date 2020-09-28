package com.nikosar.animeforever.shikimori

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class ShikimoriImplTest @Autowired constructor(
        private val shikimoriImpl: ShikimoriImpl
) : AnimeForeverApplicationTests() {

    @Test
    fun animeSearch() {
        StepVerifier.create(shikimoriImpl.ongoings())
                .assertNext { it.isNotEmpty() }
                .verifyComplete()
    }
}