package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

/**
 * Exception, that will thrown if the outcome of a sent message with reply did fail.
 */
class ReplyException @PublishedApi internal constructor(
    val failureCode: Int,
    val failureType: FailureType,
    message: String
) : Exception(message)

enum class FailureType {
    NO_HANDLER,
    TIMEOUT,
    RECIPIENT_FAILURE;

    companion object {
        fun parse(failureTypeString: String) = valueOf(failureTypeString)
    }
}
