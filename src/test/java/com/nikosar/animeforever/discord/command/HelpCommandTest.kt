package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

internal class HelpCommandTest : AnimeForeverApplicationTests() {
    @Autowired
    lateinit var helpCommand: HelpCommand

    @Test
    fun helpMePlease() {
        val helpMePlease = helpCommand.helpMePlease("", Mockito.mock(MessageReceivedEvent::class.java))
        assertThat(helpMePlease, containsString("[!mockf, !mockfind] $BRAVE_TEST_PHRASE"))
    }
}