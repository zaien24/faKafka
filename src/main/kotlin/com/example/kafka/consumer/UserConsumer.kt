package com.example.kafka.consumer

import com.example.kafka.domain.User
import com.example.kafka.dto.UserRegisteredEvent
import com.example.kafka.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import com.fasterxml.jackson.module.kotlin.readValue


@Component
class UserConsumer(
    private val userRepository: UserRepository
) {

    private val objectMapper = jacksonObjectMapper()

    @RetryableTopic(
        attempts = "3",
        backoff = Backoff(delay = 2000), // 2초 간격 재시도
        dltTopicSuffix = ".DLT",
        autoCreateTopics = "true"
    )
    @KafkaListener(topics = ["user-topic"], groupId = "user-group")
    fun consume(message: String) {
        println("📥 Kafka 수신: $message")

        if (message.contains("\"userId\":999")) {
            println("💥 의도적인 예외 발생 (userId: 999)")
            throw RuntimeException("실패!")
        }

        val event = objectMapper.readValue<UserRegisteredEvent>(message)

        val user = User(
            userId = event.userId,
            email = event.email
        )

        userRepository.save(user)
        println("✅ DB 저장 완료: userId=${user.userId}, email=${user.email}")
    }

    @KafkaListener(topics = ["user-topic.DLT"], groupId = "dlt-group")
    fun handleDlt(message: String) {
        println("🛑 DLT 메시지 수신됨: $message")
        // 필요시 DB 저장, 로그 전송, 알람 등 추가 가능
    }
}