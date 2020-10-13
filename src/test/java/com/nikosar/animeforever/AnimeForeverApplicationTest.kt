package com.nikosar.animeforever

import com.nikosar.animeforever.discord.JDABot
import io.mockk.every
import io.mockk.mockk
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.concurrent.CompletableFuture

@SpringBootTest
internal open class AnimeForeverApplicationTests {
    @MockBean
    lateinit var jdaBot: JDABot

    @Test
    fun contextLoads() {
    }
}

fun mockEvent(): MessageReceivedEvent {
    val event = mockk<MessageReceivedEvent>()
    val channel = mockk<MessageChannel>()
    val messageAction = mockk<MessageAction>()
    every { event.channel } returns channel
    every { channel.sendMessage(any<String>()) } answers { call ->
        val future = CompletableFuture<Message>()
        val message = MessageBuilder(call.invocation.args[0] as String).build()
        future.complete(message)

        every { messageAction.submit() } returns future
        messageAction
    }
    return event
}