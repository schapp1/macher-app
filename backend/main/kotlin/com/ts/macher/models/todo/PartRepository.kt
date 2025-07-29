package com.ts.macher.models.todo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PartRepository : ReactiveMongoRepository<Part, UUID>
