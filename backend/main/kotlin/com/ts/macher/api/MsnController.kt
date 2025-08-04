package com.ts.macher.api

import com.ts.macher.model.msn.Msn
import com.ts.macher.model.msn.MsnService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/msn")
@CrossOrigin(
    origins = ["http://localhost:4200"],
    methods = [
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.DELETE
    ]

)
class MsnController(
    @Autowired
    private val msnService: MsnService,
) {
    @GetMapping
    fun findAll(): Flux<Msn> = msnService.findAll()

    @PostMapping
    fun createMsn(@RequestBody msn: Msn): Mono<Msn> = msnService.create(msn)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) : Mono<Void> = msnService.deleteById(id)

}
