package com.example.kafka.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class UserConsumer {

    @RetryableTopic(
        attempts = "3",
        backoff = Backoff(delay = 2000), // 2초 간격 재시도
        dltTopicSuffix = ".DLT",
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = ["user-topic"], groupId = "user-group")
    fun consume(message: String) {
        if (message.contains("\"userId\":999")) {
            println("💥 의도적인 예외 발생")
            throw RuntimeException("실패!")
        }
        println("✅ 메시지 처리 완료: $message")
    }

    @KafkaListener(topics = ["user-topic.DLT"], groupId = "dlt-group")
    fun handleDlt(message: String) {
        println("🛑 DLT 메시지 수신됨: $message")
    }
}