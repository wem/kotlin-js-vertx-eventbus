package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.EventBusErrorJs
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.EventBusJs
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.EventBusJsOptions
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.MessageHandlerJs
import mu.KotlinLogging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

/**
 * JS client side event bus. This client is able to connect to a running Vert.x SockJS event bus bridge on the server side.
 * @see https://vertx.io/docs/vertx-sockjs-service-proxy/kotlin/
 */
class EventBus internal constructor(
    @PublishedApi internal val eventBusJs: EventBusJs
) {

    companion object {
        val logger = KotlinLogging.logger("EventBus")

        suspend fun create(url: String, options: EventBusOptions = EventBusOptions()): EventBus {
            return suspendCoroutine { cont ->
                EventBusJs(url, options.toJsOptions()).apply {
                    onopen = {
                        applyFinalConfiguration(options)
                        cont.resume(EventBus(this))
                    }
                }
            }
        }

        private fun EventBusJs.applyFinalConfiguration(options: EventBusOptions) {
            enablePing(options.enablePing)
            enableReconnect(options.enableReconnect)
        }
    }

    /**
     * Registers a consumer on the given [address].
     */
    inline fun <reified V> consumer(
        address: Address,
        crossinline consumer: Consumer<V>
    ): MessageConsumer {

        val deserializer = EventBusCodecs.getDeserializer<V>()

        val receiver = MessageReceiver(this, deserializer, object : MessageHandler<V> {
            override fun onSuccess(msg: Message<V>) {
                consumer(msg)
            }

            override fun onFailure(exception: ReplyException) {
                logger.error(
                    EventBusException("Failed to received message on address \"$address\"", exception)
                ) { "Unable to deliver message to Consumer on address \"$address\"" }
            }
        })

        val handler = receiver::onMessage

        eventBusJs.registerHandler(address, handler)

        return MessageConsumer(address, eventBusJs, handler)
    }

    inline fun <reified V : Any> send(
        address: Address,
        msg: V? = null,
        headers: MessageHeaders? = null
    ) {
        val payload = EventBusCodecs.serializeSafe(msg)
        val nativeHeaders = Headers.mapToJsHeaders(headers)

        eventBusJs.send(address, payload, nativeHeaders)
    }

    /**
     * Sends a message and awaits of a reply.
     */
    inline fun <reified V : Any, reified R : Any> request(
        address: Address,
        msg: V? = null,
        headers: MessageHeaders? = null,
    ): Promise<Message<R>> {
        return Promise { resolve, reject ->
            val payload = EventBusCodecs.serializeSafe(msg)
            val replyDeserializer = EventBusCodecs.getDeserializer<R>()
            val nativeHeaders = Headers.mapToJsHeaders(headers)

            val receiver = MessageReceiver(this, replyDeserializer, object : MessageHandler<R> {
                override fun onSuccess(msg: Message<R>) {
                    resolve(msg)
                }

                override fun onFailure(exception: ReplyException) {
                    reject(exception)
                }
            })
            eventBusJs.send(address, payload, nativeHeaders, receiver::onMessage)
        }
    }

    /**
     * Publishes an event to the given [address]
     */
    inline fun <reified V : Any> publish(
        address: Address,
        msg: V? = null,
        headers: MessageHeaders? = null
    ) {
        val nativeHeaders = Headers.mapToJsHeaders(headers)

        val payload = if (msg != null) {
            EventBusCodecs.getSerializer<V>().serialize(msg)
        } else null

        eventBusJs.publish(address, payload, nativeHeaders)
    }

    fun registerReconnectListener(listener: ReconnectListener): Listener {
        eventBusJs.onreconnect = listener
        return Listener { eventBusJs.onreconnect = {} }
    }

    fun registerErrorListener(listener: ErrorListener): Listener {
        eventBusJs.onerror = {
            listener(BusError.fromJs(it))
        }
        return Listener { eventBusJs.onerror = {} }
    }

    fun registerCloseListener(listener: CloseListener): Listener {
        eventBusJs.onclose = {
            listener(it)
        }
        return Listener { eventBusJs.onclose = {} }
    }

    fun close() {
        eventBusJs.close()
    }
}

data class EventBusOptions(
    val pingInterval: Int? = null,
    val reconnectAttemptsMax: Int? = null,
    val reconnectDelayMin: Int? = null,
    val reconnectDelayMax: Int? = null,
    val reconnectExponent: Int? = null,
    val randomizeFactor: Double? = null,
    val enablePing: Boolean = true,
    val enableReconnect: Boolean = true
) {
    fun toJsOptions() = object : EventBusJsOptions {
        override val vertxBusPingInterval = pingInterval
        override val vertxBusReconnectAttemptsMax = reconnectAttemptsMax
        override val vertxBusReconnectDelayMin = reconnectDelayMin
        override val vertxBusReconnectDelayMax = reconnectAttemptsMax
        override val vertxBusReconnectExponent = reconnectExponent
        override val vertxBusRandomizationFactor = randomizeFactor
    }
}

class MessageConsumer(
    private val address: Address,
    private val eventBusJs: EventBusJs,
    private val msgHandlerJs: MessageHandlerJs
) {
    fun unregister() {
        eventBusJs.unregisterHandler(address, msgHandlerJs)
    }
}

class Listener internal constructor(private val removeBlock: () -> Unit) {
    fun remove() {
        removeBlock()
    }
}

data class BusError internal constructor(
    val type: String,
    val body: String
) {
    companion object {
        fun fromJs(errorJs: EventBusErrorJs) = BusError(errorJs.type, errorJs.body)
    }
}
