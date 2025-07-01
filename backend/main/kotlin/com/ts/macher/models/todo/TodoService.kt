package com.ts.macher.models.todo

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {

    fun findAll(): Flux<Todo> = todoRepository.findAll()

    fun create(todo: Todo): Mono<Todo> = todoRepository.save(todo)
}
