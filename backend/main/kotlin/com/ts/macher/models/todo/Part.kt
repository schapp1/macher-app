package com.ts.macher.models.todo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID
import java.util.UUID.randomUUID

@Document("parts")
data class Part(
    @Id
    val id: UUID = randomUUID(),
    val idlNumber: String,
    val partNumber: String,
    val level: String,
    val isAssy: Boolean = false,
    val children: List<Part> = emptyList(),
    val matShortText: String?,
)
