package com.ts.macher.models.todo

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {

    fun findAll(): Flux<Todo> = todoRepository.findAll()

    fun create(todo: Todo): Mono<Todo> = todoRepository.save(todo)

    fun deleteById(id: UUID): Mono<Void> = todoRepository.deleteById(id)

    fun processExcelFile(file: MultipartFile): Mono<Void> {
        return Mono.fromCallable {
            val workbook = XSSFWorkbook(file.inputStream)
            val sheet = workbook.getSheetAt(0)
            val headerRow = sheet.getRow(0)

            // Finde den Index der "todo"-Spalte
            val todoColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "todo" }
                ?: throw IllegalArgumentException("Keine 'todo'-Spalte in der Excel-Datei gefunden")

            val todos = mutableListOf<Todo>()

            // Ab Zeile 1 (nach der Überschriftenzeile) alle Zeilen durchlaufen
            for (i in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i) ?: continue
                val todoText = row.getCell(todoColumnIndex)?.toString()?.trim() ?: continue

                if (todoText.isNotBlank()) {
                    todos.add(Todo(
                        id = UUID.randomUUID(),
                        title = todoText
                    ))
                }
            }

            workbook.close()
            todos
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany { todoList ->
                todoRepository.saveAll(todoList)
            }
            .then()
    }
}
