package ch.sourcemotion.vertx.kotlin.eventbus.browser.core

import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodec
import ch.sourcemotion.vertx.kotlin.eventbus.browser.core.codec.EventBusCodecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class EventBusCodecTest {

    @Test
    fun serializeSafe_with_null() {
        EventBusCodecs.registerCodec(StringFieldDtoCodec)
        assertNull(EventBusCodecs.serializeSafe<StringFieldDto>(null).unsafeCast<Any?>())
    }

    @Test
    fun serializeSafe_string_value() {
        EventBusCodecs.registerCodec(StringFieldDtoCodec)

        val expectedValue = "some-value"
        val serialized = EventBusCodecs.serializeSafe(StringFieldDto(expectedValue)).unsafeCast<String>()
        assertEquals(expectedValue, serialized)
    }

    @Test
    fun serializeSafe_bytearray_value() {
        EventBusCodecs.registerCodec(ByteArrayFieldDtoCodec)

        val expectedValue = ByteArray(1) { 1 }
        val serialized = EventBusCodecs.serializeSafe(ByteArrayFieldDto(expectedValue)).unsafeCast<ByteArray>()
        assertEquals(expectedValue, serialized)
    }

    @Test
    fun deserializeSafe_with_null_and_undefined() {
        assertNull(
            EventBusCodecs.deserializeSafe<Any>({ fail("Deserializer should not get called") }, null)
                .unsafeCast<Any?>()
        )
        assertNull(
            EventBusCodecs.deserializeSafe<Any>({ fail("Deserializer should not get called") }, undefined)
                .unsafeCast<Any?>()
        )
    }

    @Test
    fun deserializeSafe_unit_with_null_and_undefined() {
        assertEquals(
            Unit,
            EventBusCodecs.deserializeSafe<Unit>({ fail("Deserializer should not get called") }, null)
                .unsafeCast<Any?>()
        )
        assertEquals(
            Unit,
            EventBusCodecs.deserializeSafe<Unit>({ fail("Deserializer should not get called") }, undefined)
                .unsafeCast<Any?>()
        )
    }
}

data class StringFieldDto(val field: String)

object StringFieldDtoCodec : EventBusCodec<StringFieldDto> {
    override fun serialize(value: StringFieldDto) = value.field
    override fun deserialize(value: dynamic): StringFieldDto = StringFieldDto(value.unsafeCast<String>())
}

data class ByteArrayFieldDto(val field: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayFieldDto) return false

        if (!field.contentEquals(other.field)) return false

        return true
    }

    override fun hashCode(): Int {
        return field.contentHashCode()
    }
}

object ByteArrayFieldDtoCodec : EventBusCodec<ByteArrayFieldDto> {
    override fun serialize(value: ByteArrayFieldDto) = value.field
    override fun deserialize(value: dynamic): ByteArrayFieldDto = ByteArrayFieldDto(value.unsafeCast<ByteArray>())
}
