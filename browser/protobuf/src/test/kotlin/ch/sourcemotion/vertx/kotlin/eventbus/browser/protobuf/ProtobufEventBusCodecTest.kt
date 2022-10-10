package ch.sourcemotion.vertx.kotlin.eventbus.browser.protobuf

import ch.sourcemotion.vertx.kotlin.eventbus.browser.codec.protobuf.ProtobufEventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.EventBus
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.EventBusOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtobufEventBusCodecTest {

    @BeforeTest
    fun registerCodecs() {
        ProtobufEventBusCodec.register(Dto.serializer())
    }

    @Test
    fun request() = GlobalScope.promise {
        val eventBus = createEventBus()

        val expectedDto = Dto("Check")
        val answer = eventBus.request<Dto, Dto>("/request", expectedDto).await()
        assertEquals(expectedDto, answer.body)
    }

    private suspend fun createEventBus() =
        EventBus.create("http://localhost:9999/eventbus", EventBusOptions(enablePing = false))
}

@Serializable
data class Dto(val field: String)
