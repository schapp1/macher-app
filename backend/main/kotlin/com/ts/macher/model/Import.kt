package com.ts.macher.model

import com.ts.macher.model.part.Part
import com.ts.macher.model.part.PartRepository
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.Stack
import java.util.UUID

@Service
class Import(
    private val partRepository: PartRepository,
) {    fun importExcelFile(file: MultipartFile): Flux<Part> {
    return Mono.fromCallable {
        val workbook = WorkbookFactory.create(file.inputStream)
        val sheet = workbook.getSheetAt(0)

        val headerRow = sheet.getRow(0)
        val partNumberIndex = findColumnIndex(headerRow, "partnummer")
        val levelIndex = findColumnIndex(headerRow, "auflösungsstufe")
        val matShortTextIndex = findColumnIndex(headerRow, "materialkurztext")

        val parentStack = Stack<ParentInfo>()
        val partsMap = mutableMapOf<String, Part>() // Alle Parts nach PartNumber
        val hierarchyMap = mutableMapOf<String, MutableList<Part>>()

        for (i in 1 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(i) ?: continue

            val partNumber = row.getCell(partNumberIndex)?.toString()?.trim() ?: ""
            val levelText = row.getCell(levelIndex)?.toString()?.trim() ?: ""
            val matShortText = row.getCell(matShortTextIndex)?.toString()?.trim() ?: ""

            val level = levelText.filter { it.isDigit() }.toIntOrNull() ?: 0

            if (partNumber.isBlank() && levelText.isNotBlank()) {
                while (parentStack.isNotEmpty() && parentStack.peek().level >= level) {
                    parentStack.pop()
                }
                continue
            }

            if (partNumber.isNotBlank() && level >= 3) {
                var parent: String? = null

                while (parentStack.isNotEmpty() && parentStack.peek().level >= level) {
                    parentStack.pop()
                }

                if (parentStack.isNotEmpty() && parentStack.peek().level == level - 1) {
                    parent = parentStack.peek().partNumber
                }

                val part = Part(
                    id = UUID.randomUUID(),
                    partNumber = partNumber,
                    matShortText = matShortText,
                    level = level,
                    parent = parent
                )

                partsMap[partNumber] = part

                if (parent != null) {
                    hierarchyMap.computeIfAbsent(parent) { mutableListOf() }.add(part)
                }

                parentStack.push(ParentInfo(level, partNumber))
            }
        }

        // Verknüpfe Kind-Elemente mit den Eltern
        partsMap.values.forEach { part ->
            part.children = hierarchyMap[part.partNumber] ?: emptyList()
        }

        // Nur Root-Elemente (ohne Parent) zum Speichern auswählen
        val rootParts = partsMap.values.filter { it.parent == null }

        workbook.close()
        rootParts
    }
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany { partRepository.saveAll(it) }
}

    private fun findColumnIndex(headerRow: org.apache.poi.ss.usermodel.Row, columnName: String): Int {
        return (0 until headerRow.physicalNumberOfCells)
            .find { headerRow.getCell(it)?.toString()?.lowercase()?.contains(columnName.lowercase()) == true }
            ?: throw IllegalArgumentException("Spalte '$columnName' nicht gefunden")
    }


    private data class ParentInfo(val level: Int, val partNumber: String)
}

