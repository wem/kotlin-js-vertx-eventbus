package ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js

internal typealias OnOpenHandler = () -> Unit
internal typealias OnCloseHandler = (dynamic) -> Unit
internal typealias OnReconnectHandler = () -> Unit
internal typealias BusErrorHandler = (EventBusErrorJs) -> Unit
internal typealias MessageHandlerJs = (ErrorMessageJs?, MessageJs?) -> Unit

@JsModule("@vertx/eventbus-bridge-client.js")
@JsNonModule
@JsName("EventBus")
external class EventBusJs(url: String, options: EventBusJsOptions? = definedExternally) {
    @JsName("onopen")
    var onopen: OnOpenHandler

    @JsName("onclose")
    var onclose: OnCloseHandler

    @JsName("onreconnect")
    var onreconnect: OnReconnectHandler

    @JsName("onerror")
    var onerror: BusErrorHandler

    @JsName("defaultHeaders")
    var defaultHeaders: dynamic

    @JsName("close")
    fun close()

    @JsName("enablePing")
    fun enablePing(enable: Boolean)

    @JsName("enableReconnect")
    fun enableReconnect(enable: Boolean)

    @JsName("registerHandler")
    fun registerHandler(address: String, handler: MessageHandlerJs)

    @JsName("unregisterHandler")
    fun unregisterHandler(address: String, handler: MessageHandlerJs)

    @JsName("send")
    fun send(
        address: String,
        msg: dynamic = definedExternally,
        headers: dynamic = definedExternally,
        replyHandler: MessageHandlerJs? = definedExternally
    )

    @JsName("publish")
    fun publish(
        address: String,
        msg: dynamic = definedExternally,
        headers: dynamic = definedExternally
    )
}

/**
 * Error in application layer. Means in Vertx / message handler
 */
external interface ErrorMessageJs {
    @JsName("failureCode")
    val failureCode: Int
    @JsName("failureType")
    val failureType: String
    @JsName("message")
    val message: String
}

/**
 * Error on SockJS
 */
external interface EventBusErrorJs {
    @JsName("type")
    val type: String
    @JsName("body")
    val body: String
}

/**
 * Native Vert.x event bus message, received over SockJS.
 */
external interface MessageJs {
    @JsName("address")
    val address: String
    @JsName("replyAddress")
    val replyAddress: String?
    @JsName("body")
    val body: dynamic
    @JsName("headers")
    val headers: dynamic
    @JsName("type")
    val type: String?
}

external interface EventBusJsOptions {
    @JsName("vertxbus_ping_interval")
    val vertxBusPingInterval: Int?
    @JsName("vertxbus_reconnect_attempts_max")
    val vertxBusReconnectAttemptsMax: Int?
    @JsName("vertxbus_reconnect_delay_min")
    val vertxBusReconnectDelayMin: Int?
    @JsName("vertxbus_reconnect_delay_max")
    val vertxBusReconnectDelayMax: Int?
    @JsName("vertxbus_reconnect_exponent")
    val vertxBusReconnectExponent: Int?
    @JsName("vertxbus_randomization_factor")
    val vertxBusRandomizationFactor: Double?
}

