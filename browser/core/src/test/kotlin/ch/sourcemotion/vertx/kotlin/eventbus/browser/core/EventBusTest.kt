package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.*

class EventBusTest {

    companion object {
        const val STRING_MSG = "simple-string-message"
        val complexMsg = ComplexType(STRING_MSG)
    }

    private lateinit var eventBus: EventBus

    @BeforeTest
    fun registerCodecs() {
        EventBusCodecs.registerCodec(ComplexTypeCodec)
    }

    @AfterTest
    internal fun tearDown() {
        eventBus.close()
    }

    @Test
    fun connection_test() = GlobalScope.promise {
        eventBus = EventBus.create("http://localhost:9999/eventbus")
    }

    @Test
    fun consumer_string_value() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/consumer") {
                assertEquals(STRING_MSG, it.body, "Received message is not the expected one")
                assertTrue(it.headers.isEmpty(), "Headers should be null or empty")
                cont.resume(Unit)
            }
        }
    }

    @Test
    fun consumer_complex_type() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/consumer-complex-type") {
                assertEquals(
                    ComplexType(STRING_MSG),
                    it.body,
                    "Received message is not the expected one"
                )
                assertTrue(it.headers.isEmpty(), "Headers should be null or empty")
                cont.resume(Unit)
            }
        }
    }

    @Test
    fun register_consumer_and_consume_with_headers() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/consumer-with-headers") {
                assertEquals(STRING_MSG, it.body, "Received message is not the expected one")

                val headers = it.headers
                assertNotNull(headers, "Headers are null")
                assertTrue(
                    headers.containsKey("header-key"),
                    "Message headers not contains expected header \"header-key\""
                )
                assertEquals("header-value", headers["header-key"], "Not expected header value")

                cont.resume(Unit)
            }
        }
    }

    @Test
    fun request_string() = GlobalScope.promise {
        eventBus = createEventBus()

        val answer = eventBus.request<String, String>(
            "/request",
            STRING_MSG
        ).await()

        assertEquals(STRING_MSG, answer.body, "Received message is not the expected one")
        assertTrue(answer.headers.isEmpty(), "Headers should be null or empty")
    }

    @Test
    fun request_complex_type() = GlobalScope.promise {
        eventBus = createEventBus()

        val answer = eventBus.request<ComplexType, ComplexType>(
            "/request",
            complexMsg
        ).await()

        assertEquals(complexMsg, answer.body, "Received message is not the expected one")
        assertTrue(answer.headers.isEmpty(), "Headers should be null or empty")
    }

    @Test
    fun request_with_headers() = GlobalScope.promise {
        eventBus = createEventBus()

        val headers = mapOf("header-key" to "header-value")

        val answer = eventBus.request<ComplexType, ComplexType>(
            "/request",
            complexMsg,
            headers,
        ).await()

        assertEquals(complexMsg, answer.body, "Received message is not the expected one")

        val responseHeaders = answer.headers
        assertNotNull(responseHeaders, "Response headers are null")
        assertEquals(headers, responseHeaders, "Not expected header")
    }

    @Test
    fun request_recipient_failure() = GlobalScope.promise {
        eventBus = createEventBus()

        val exception = assertFailsWith(ReplyException::class) {
            eventBus.request<Unit, Unit>("/request-recipient-failure").await()
        }

        assertEquals(1000, exception.failureCode, "Recipient failure has wrong code")
        assertEquals(FailureType.RECIPIENT_FAILURE, exception.failureType)
        assertEquals("Server side did fail", exception.message)
    }

    @Test
    fun request_no_handler_failure() = GlobalScope.promise {
        eventBus = createEventBus()

        val exception = assertFailsWith(ReplyException::class) {
            eventBus.request<Unit, Unit>("/request-no-handler-failure").await()
        }

        assertEquals(-1, exception.failureCode, "Recipient failure has wrong code")
        assertEquals(FailureType.NO_HANDLER, exception.failureType)
        assertEquals("No Handler there", exception.message)
    }

    @Test
    fun request_timeout_failure() = GlobalScope.promise {
        eventBus = createEventBus()

        val exception = assertFailsWith(ReplyException::class) {
            eventBus.request<Unit, Unit>("/request-timeout-failure").await()
        }

        assertEquals(-1, exception.failureCode, "Recipient failure has wrong code")
        assertEquals(FailureType.TIMEOUT, exception.failureType)
        assertEquals("Server side did run into timeout", exception.message)
    }

    @Test
    fun reply_on_reply_complex_type() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine { cont ->
            GlobalScope.launch {
                val answer = eventBus.request<ComplexType, ComplexType>("/reply-expected", complexMsg).await()
                assertEquals(complexMsg, answer.body)
                val replyMsg = complexMsg.copy(field = "other-value")
                val replyOfReplyAnswer = answer.replyAndRequest<ComplexType, ComplexType>(replyMsg).await()
                assertEquals(replyMsg, replyOfReplyAnswer.body)
                cont.resume(Unit)
            }
        }
    }

    @Test
    fun publish() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/publish-ack") {
                assertEquals(complexMsg, it.body)
                cont.resume(Unit)
            }
            eventBus.publish("/publish", complexMsg)
        }
    }

    @Test
    fun send() = GlobalScope.promise {
        eventBus = createEventBus()

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/send-ack") {
                assertEquals(complexMsg, it.body)
                cont.resume(Unit)
            }
            eventBus.send("/send", complexMsg)
        }
    }

    @Test
    fun unregister() = GlobalScope.promise {
        eventBus = createEventBus()

        val consumerAddress = "some-address"

        suspendCoroutine<Unit> { cont ->
            eventBus.consumer("/unregister-ack") {
                assertEquals(consumerAddress, it.body)
                cont.resume(Unit)
            }
            eventBus.consumer<Unit>(consumerAddress) {}.unregister()
        }
    }

    private suspend fun createEventBus() =
        EventBus.create("http://localhost:9999/eventbus", EventBusOptions(enablePing = false))
}

data class ComplexType(val field: String)

object ComplexTypeCodec : EventBusCodec<ComplexType> {
    override fun serialize(value: ComplexType) : dynamic {
        val result = js("{}")
        result.field = value.field
        return result
    }
    override fun deserialize(value: dynamic) = ComplexType(value.field.unsafeCast<String>())
}
