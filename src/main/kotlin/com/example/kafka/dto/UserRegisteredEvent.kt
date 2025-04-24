package com.example.kafka.dto

data class UserRegisteredEvent(
    val userId: Long,
    val email: String
)