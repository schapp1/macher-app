package com.ts.macher.models.todo

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.UUID

@Service
class PartService(
    private val partRepository: PartRepository,
) {

    fun findAll(): Flux<Part> = partRepository.findAll()

    fun create(part: Part): Mono<Part> = partRepository.save(part)

    fun deleteById(id: UUID): Mono<Void> = partRepository.deleteById(id)

    fun deleteAll(): Mono<Void> = partRepository.deleteAll()

    fun processExcelFile(file: MultipartFile): Mono<Void> {
        return Mono.fromCallable {
            val workbook = XSSFWorkbook(file.inputStream)
            val sheet = workbook.getSheetAt(0)
            val headerRow = sheet.getRow(0)

            // Finde den Index der "todo"-Spalte
            val todoColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "todo" }
                ?: throw IllegalArgumentException("Keine 'todo'-Spalte in der Excel-Datei gefunden")

            val parts = mutableListOf<Part>()

            // Ab Zeile 1 (nach der Überschriftenzeile) alle Zeilen durchlaufen
            for (i in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i) ?: continue
                val todoText = row.getCell(todoColumnIndex)?.toString()?.trim() ?: continue

                if (todoText.isNotBlank()) {
                    parts.add(Part(
                        id = UUID.randomUUID(),
                        title = todoText
                    ))
                }
            }

            workbook.close()
            parts
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany { todoList ->
                partRepository.saveAll(todoList)
            }
            .then()
    }
}
