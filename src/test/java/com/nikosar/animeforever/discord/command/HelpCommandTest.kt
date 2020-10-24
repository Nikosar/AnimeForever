package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.mockEvent
import net.dv8tion.jda.api.entities.Message
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class HelpCommandTest : AnimeForeverApplicationTests() {
    @Autowired
    lateinit var helpCommand: HelpCommand

    @Test
    fun helpMePlease() {
        val event = mockEvent()
        val block: Message? = helpCommand.helpMePlease("", event).block() as Message?
        val contentRaw = block?.contentRaw!!
        assertThat(contentRaw, containsString("[!mockf, !mockfind] -> $BRAVE_TEST_PHRASE"))
    }
}