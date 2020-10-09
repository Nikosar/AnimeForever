package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.discord.command.processor.CommandFactory
import com.nikosar.animeforever.discord.command.processor.CommandNotFoundException
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

internal class CommandFactoryTest : AnimeForeverApplicationTests() {
    @Autowired
    private lateinit var commandFactory: CommandFactory

    @Test
    fun commandCreatedSuccessful() {
        var createCommand = commandFactory.createCommand("!mockfind")
        var block = createCommand.execute("123", Mockito.mock(MessageReceivedEvent::class.java)).block()
        assertEquals("123", block)

        createCommand = commandFactory.createCommand("!mockf")
        block = createCommand.execute("123", Mockito.mock(MessageReceivedEvent::class.java)).block()
        assertEquals("123", block)

        createCommand = commandFactory.createCommand("!testcommand")
        block = createCommand.execute("123", Mockito.mock(MessageReceivedEvent::class.java)).block()
        assertEquals("test", block)
    }

    @Test
    fun failOnWrongCommand() {
        assertThrows<CommandNotFoundException> { commandFactory.createCommand("!notExistedCommand") }
    }
}