package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class HelpCommandTest : AnimeForeverApplicationTests() {
    @Autowired
    lateinit var helpCommand: HelpCommand

    @Test
//    TODO think about how to test
    fun helpMePlease() {
//        val helpMePlease = helpCommand.helpMePlease("", Mockito.mock(MessageReceivedEvent::class.java)).block()
//        assertThat(helpMePlease, containsString("[!mockf, !mockfind] $BRAVE_TEST_PHRASE"))
    }
}