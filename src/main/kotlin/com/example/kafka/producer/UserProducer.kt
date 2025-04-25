package com.example.kafka.producer

import com.example.kafka.dto.UserRegisteredEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.ObjectMapper
import io.opentelemetry.api.trace.Span
import java.nio.charset.StandardCharsets

@Service
class UserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun sendWithHeader(event: UserRegisteredEvent) {
        val json = objectMapper.writeValueAsString(event)
        val record = ProducerRecord<String, String>("user-topic", event.userId.toString(), json).apply {
            headers().add(RecordHeader("eventType", "UserRegistered".toByteArray()))
            val traceId = Span.current().spanContext.traceId
            headers().add(RecordHeader("traceId", traceId.toByteArray(StandardCharsets.UTF_8)))
        }
        kafkaTemplate.send(record)
        println("🔥 Sent with headers: $json")
    }
}