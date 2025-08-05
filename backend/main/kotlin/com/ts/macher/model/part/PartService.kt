package com.ts.macher.model.part

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class PartService(
    private val partRepository: PartRepository,
) {

    fun findAll(): Flux<Part> = partRepository.findAll()

    fun create(part: Part): Mono<Part> = partRepository.save(part)

    fun deleteById(id: UUID): Mono<Void> = partRepository.deleteById(id)

    fun deleteAll(): Mono<Void> = partRepository.deleteAll()
}
