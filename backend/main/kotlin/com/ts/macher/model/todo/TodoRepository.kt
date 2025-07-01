package com.ts.macher.model.todo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TodoRepository : ReactiveMongoRepository<Todo, UUID>
