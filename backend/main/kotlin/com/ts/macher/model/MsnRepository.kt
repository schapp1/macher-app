package com.ts.macher.model

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MsnRepository : ReactiveMongoRepository<Msn, UUID>
