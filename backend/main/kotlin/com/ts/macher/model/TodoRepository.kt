package com.ts.macher.model

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : MongoRepository<Todos, Int>
