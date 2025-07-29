package com.ts.macher.models.todo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID
import java.util.UUID.randomUUID

@Document("parts")
data class Part(
    @Id
    val id: UUID = randomUUID(),
    val title: String,
)
