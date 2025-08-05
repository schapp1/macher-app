package com.ts.macher.api

import com.ts.macher.model.Import
import com.ts.macher.model.part.PartService
import com.ts.macher.model.part.Part
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.UUID

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(
    origins = ["http://localhost:4200"],
    methods = [
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.DELETE
    ]

)
class PartController(
    @Autowired
    private val partService: PartService,
    private val importService: Import,
) {

    @GetMapping
    fun findAll(): Flux<Part> = partService.findAll()

    @PostMapping
    fun create(@RequestBody part: Part) = partService.create(part)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): Mono<Void> = partService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = partService.deleteAll()

    @PostMapping("/upload-excel", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importExcel(@RequestParam("file") file: MultipartFile): Flux<Part> {
        return importService.importExcelFile(file)
    }
}
