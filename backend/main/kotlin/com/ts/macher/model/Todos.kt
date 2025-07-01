package com.ts.macher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("todos")
data class Todos(
    @Id
    val id: Int? = null,
    val title: String,
)
