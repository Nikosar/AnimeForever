package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.discord.command.processor.CommandFactory
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class CommandFactoryTest : AnimeForeverApplicationTests() {
    @Autowired
    private lateinit var commandFactory: CommandFactory

    @Test
    fun commandCreatedSuccessful() {
        var createCommand = commandFactory.createCommand("!mockfind")
        StepVerifier.create(createCommand!!.execute("123", Mockito.mock(MessageReceivedEvent::class.java)))
                .assertNext { it == "123" }
                .verifyComplete()

        createCommand = commandFactory.createCommand("!mockf")
        StepVerifier.create(createCommand!!.execute("123", Mockito.mock(MessageReceivedEvent::class.java)))
                .assertNext { it == "123" }
                .verifyComplete()

        createCommand = commandFactory.createCommand("!testcommand")
        StepVerifier.create(createCommand!!.execute("123", Mockito.mock(MessageReceivedEvent::class.java)))
                .assertNext { it == "test" }
                .verifyComplete()
    }
}