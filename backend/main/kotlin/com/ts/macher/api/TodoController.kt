package com.ts.macher.api

import com.ts.macher.models.todo.TodoService
import com.ts.macher.models.todo.Todo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = ["http://localhost:4200"])
class TodoController(
    @Autowired
    private val todoService: TodoService,
) {

    @GetMapping
    fun findAll(): Flux<Todo> = todoService.findAll()

    @PostMapping
    fun create(@RequestBody todo: Todo) = todoService.create(todo)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): Mono<Void> = todoService.deleteById(id)

    @PostMapping("/upload-excel", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadExcel(@RequestParam("file") file: MultipartFile): Mono<String> {
        return todoService.processExcelFile(file)
            .then(Mono.just("Excel-Datei erfolgreich verarbeitet"))
            .onErrorResume { e ->
                Mono.error(RuntimeException("Fehler beim Verarbeiten der Excel-Datei: ${e.message}"))
            }
    }
}
