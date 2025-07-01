package com.ts.macher.model

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TodoRepository : MongoRepository<Todos, UUID>
