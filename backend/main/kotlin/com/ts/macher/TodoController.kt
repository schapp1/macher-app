package com.ts.macher

import com.ts.macher.model.Todos
import com.ts.macher.model.TodoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = ["http://localhost:4200"])
class TodoController(
    @Autowired
    private val todoRepository: TodoRepository
) {

    @GetMapping
    fun getAllTodos(): List<Todos> = todoRepository.findAll()

    @PostMapping
    fun createTodo(@RequestBody todos: Todos): Todos = todoRepository.save(todos)

}
