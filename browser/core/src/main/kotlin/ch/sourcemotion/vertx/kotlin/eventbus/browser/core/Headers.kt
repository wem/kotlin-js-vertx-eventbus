package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

object Headers {

    private val jsObject = js("Object")

    @PublishedApi
    internal fun mapToJsHeaders(headers: Map<String, String>?): dynamic {
        val jsHeaders: dynamic = object {}
        headers?.forEach { jsHeaders[it.key] = it.value }
        return jsHeaders
    }

    internal fun mapFromJsHeaders(jsHeaders: dynamic): Map<String, String> =
        if (jsHeaders != null && jsHeaders != undefined) {
            val headers = HashMap<String, String>()
            val keys = jsObject.keys(jsHeaders).unsafeCast<Array<String>>()
            keys.forEach { headers[it] = jsHeaders[it] }
            headers
        } else emptyMap()
}
