package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HeadersTest {

    private val jsObject = js("Object")

    @Test
    fun map_to_js_headers() {
        val expected = mapOf("key" to "value")
        val jsHeaders = Headers.mapToJsHeaders(expected)
        assertEquals("value", jsHeaders.key)
    }

    @Test
    fun map_to_js_headers_empty() {
        val expected = emptyMap<String, String>()
        val jsHeaders = Headers.mapToJsHeaders(expected)
        assertTrue(jsObject.keys(jsHeaders).unsafeCast<Array<String>>().isEmpty())
    }

    @Test
    fun map_to_js_headers_null() {
        val jsHeaders = Headers.mapToJsHeaders(null)
        assertTrue(jsObject.keys(jsHeaders).unsafeCast<Array<String>>().isEmpty())
    }

    @Test
    fun map_from_js_headers() {
        val expected: dynamic = object {}
        expected.key = "value"
        val headers = Headers.mapFromJsHeaders(expected)
        assertNotNull(headers)
        assertTrue(headers.containsKey("key"))
        assertTrue(headers.containsValue("value"))
    }

    @Test
    fun map_from_js_headers_empty() {
        val headers = Headers.mapFromJsHeaders(object {})
        assertNotNull(headers)
        assertTrue(headers.isEmpty())
    }

    @Test
    fun map_from_js_headers_null() {
        val headers = Headers.mapFromJsHeaders(null)
        assertNotNull(headers)
        assertTrue(headers.isEmpty())
    }

    @Test
    fun map_from_js_headers_undefined() {
        val headers = Headers.mapFromJsHeaders(undefined)
        assertNotNull(headers)
        assertTrue(headers.isEmpty())
    }
}
