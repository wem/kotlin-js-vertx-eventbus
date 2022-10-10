package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.Deserializer
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.ErrorMessageJs
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.js.MessageJs

interface MessageHandler<M> {
    fun onSuccess(msg: Message<M>)
    fun onFailure(exception: ReplyException)
}

class MessageReceiver<M>(
    private val eventBus: EventBus,
    private val deserializer: Deserializer<M>,
    private val messageHandler: MessageHandler<M>
) {
    fun onMessage(errorMsgJs: ErrorMessageJs?, msgJs: MessageJs?) {
        when {
            errorMsgJs != null -> {
                val exception = ReplyException(
                    errorMsgJs.failureCode,
                    FailureType.parse(errorMsgJs.failureType),
                    errorMsgJs.message
                )
                messageHandler.onFailure(exception)
            }
            msgJs != null -> {
                val body = EventBusCodecs.deserializeSafe(deserializer, msgJs.body)
                val headers = Headers.mapFromJsHeaders(msgJs.headers)
                val msg = Message.create(
                    msgJs,
                    headers,
                    body,
                    eventBus
                )
                messageHandler.onSuccess(msg)
            }
            else -> {
                throw EventBusException("No valid message or error received for the consumer, so cannot continue. This should never happen.")
            }
        }
    }
}
