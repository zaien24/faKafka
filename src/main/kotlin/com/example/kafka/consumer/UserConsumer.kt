package com.example.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserConsumer {

    @KafkaListener(topics = ["user-topic"], groupId = "user-group")
    fun consume(record: ConsumerRecord<String, String>) {
        val eventType = record.headers().lastHeader("eventType")?.value()?.decodeToString()
        val traceId = record.headers().lastHeader("traceId")?.value()?.decodeToString()

        println("✅ [eventType=$eventType traceId=$traceId] message=${record.value()}")
    }
}