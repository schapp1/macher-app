package com.ts.macher.model.todo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID
import java.util.UUID.randomUUID

@Document("todos")
data class Todo(
    @Id
    val id: UUID = randomUUID(),
    val title: String,
)
