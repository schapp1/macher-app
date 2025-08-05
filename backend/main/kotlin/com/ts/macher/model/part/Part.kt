package com.ts.macher.model.part

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import java.util.UUID
import java.util.UUID.randomUUID

@Document("parts")
data class Part(
    @Id
    val id: UUID = randomUUID(),
    val partNumber: String,
    val matShortText: String,
    val level: Int,
    val parent: String? = null,
    var children: List<Part> = emptyList()
)
