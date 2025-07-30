package com.ts.macher.model

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class MsnService(
    private val msnRepository: MsnRepository,
) {

    fun findAll(): Flux<Msn> = msnRepository.findAll()

    fun create(msn: Msn): Mono<Msn> = msnRepository.save(msn)

    fun deleteById(id: UUID): Mono<Void> = msnRepository.deleteById(id)
}
