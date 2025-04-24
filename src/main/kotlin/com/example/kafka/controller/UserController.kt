package com.example.kafka.controller

import com.example.kafka.dto.UserRegisteredEvent
import com.example.kafka.producer.UserProducer
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val producer: UserProducer
) {
    @PostMapping("/register")
    fun register(@RequestBody event: UserRegisteredEvent): String {
        producer.send(event)
        return "User registered event sent!"
    }
}