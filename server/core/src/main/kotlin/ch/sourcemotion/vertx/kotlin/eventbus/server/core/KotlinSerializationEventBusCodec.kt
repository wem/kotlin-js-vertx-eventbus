package ch.sourcemotion.vertx.kotlin.eventbus.server.core

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import kotlinx.serialization.KSerializer

class KotlinSerializationEventBusCodec<T> private constructor(
    private val name: String,
    private val serializationEngine: KotlinSerializationEngine,
    private val kSerializer: KSerializer<T>
) : MessageCodec<T, T> {

    companion object {
        fun <T> create(name: String, kSerializer: KSerializer<T>, serializationEngine: KotlinSerializationEngine) =
            KotlinSerializationEventBusCodec(name, serializationEngine, kSerializer)
    }

    override fun encodeToWire(buffer: Buffer, s: T) {
        val serialized = Buffer.buffer(serializationEngine.serialize(s, kSerializer))
        buffer.appendInt(serialized.length())
        buffer.appendBuffer(serialized)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): T {
        val length = buffer.getInt(pos)
        val dataPos = pos + 4
        return serializationEngine.deserialize(buffer.slice(dataPos, dataPos + length).toString(Charsets.UTF_8), kSerializer)
    }

    override fun transform(s: T): T = s

    override fun name() = name

    override fun systemCodecID(): Byte = -1
}
