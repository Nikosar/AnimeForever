package com.nikosar.animeforever.discord.command

import com.nikosar.animeforever.AnimeForeverApplicationTests
import com.nikosar.animeforever.discord.command.processor.CommandFactory
import io.mockk.mockk
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import kotlin.reflect.full.functions

internal class CommandFactoryTest : AnimeForeverApplicationTests() {
    @Autowired
    private lateinit var commandFactory: CommandFactory

    @Test
    fun commandCreatedSuccessful() {
        val createCommand = commandFactory.createCommand("!mockfind")
        StepVerifier.create(createCommand!!.execute("123", mockk<MessageReceivedEvent>()))
                .assertNext { it == "123" }
                .verifyComplete()
    }

    @Test
    fun commandAliasWorking() {
        val createCommand = commandFactory.createCommand("!mockf")
        StepVerifier.create(createCommand!!.execute("123", mockk<MessageReceivedEvent>()))
                .assertNext { it == "123" }
                .verifyComplete()
    }

    @Test
    fun anotherCommandWorking() {
        val createCommand = commandFactory.createCommand("!testcommand")
        StepVerifier.create(createCommand!!.execute("", mockk<MessageReceivedEvent>()))
                .assertNext { it == "test" }
                .verifyComplete()
    }

    @Test
    fun commandWithoutArgsWorking() {
        val functions = TestCommandWithoutArgs::class.functions.toList()
        val kFunction = functions[0]
        val parameters = kFunction.parameters
        val map = ModelMapper().map("123", Int::class.java)
        val type = parameters[1].type

        val createCommand = commandFactory.createCommand("!noargs")
        StepVerifier.create(createCommand!!.execute("", mockk<MessageReceivedEvent>()))
                .assertNext { it == "123" }
                .verifyComplete()
    }

    @Test
    fun commandSumWith2ArgsWorking() {
        val createCommand = commandFactory.createCommand("!sum")
        StepVerifier.create(createCommand!!.execute("1n 100", mockk<MessageReceivedEvent>()))
                .assertNext { it == "101" }
                .verifyComplete()
    }
}