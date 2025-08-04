package com.ts.macher.model.msn

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID
import java.util.UUID.randomUUID

@Document("msn")
data class Msn(
    @Id
    val id: UUID = randomUUID(),
    val msnId: String
)
