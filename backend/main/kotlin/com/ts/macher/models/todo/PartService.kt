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

            val idlColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "idl-nummer" }
                ?: throw IllegalArgumentException("Keine 'IDL-Nummer'-Spalte in der Excel-Datei gefunden")
            val partNumberColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "partnummer" }
                ?: throw IllegalArgumentException("Keine 'Partnummer'-Spalte in der Excel-Datei gefunden")
            val aufloesungsstufeColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "auflösungsstufe" }
                ?: throw IllegalArgumentException("Keine 'Auflösungsstufe'-Spalte in der Excel-Datei gefunden")

            data class RowPart(
                val index: Int,
                val part: Part,
                val level: Int,
                var children: MutableList<RowPart> = mutableListOf()
            )

            val allParts = mutableListOf<RowPart>()
            var currentAssembly: RowPart? = null
            var currentSubAssembly: RowPart? = null

            for (i in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i) ?: continue
                val idlNumber = row.getCell(idlColumnIndex)?.toString()?.trim() ?: ""
                val partNumber = row.getCell(partNumberColumnIndex)?.toString()?.trim() ?: ""
                val levelStr = row.getCell(aufloesungsstufeColumnIndex)?.toString()?.trim() ?: ""
                val level = levelStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: continue

                if (idlNumber.isNotBlank() || partNumber.isNotBlank()) {
                    val rowPart = RowPart(
                        index = i,
                        part = Part(
                            id = UUID.randomUUID(),
                            idlNumber = idlNumber,
                            partNumber = partNumber,
                            level = level.toString(), // Speichere nur die Zahl
                            isAssy = false // wird später basierend auf Children gesetzt
                        ),
                        level = level
                    )

                    when {
                        level == 4 -> {
                            currentAssembly = rowPart
                            currentSubAssembly = null
                            allParts.add(rowPart)
                        }
                        level == 5 && currentAssembly != null -> {
                            currentSubAssembly = rowPart
                            currentAssembly.children.add(rowPart)
                        }
                        level > 5 && currentSubAssembly != null -> {
                            currentSubAssembly.children.add(rowPart)
                        }
                        level == 3 -> {
                            // Single Parts mit Level 3 direkt zu allParts hinzufügen
                            allParts.add(rowPart)
                        }
                    }
                }
            }

            // Konvertiere die Hierarchie in Part-Objekte und setze isAssy
            fun convertToPart(rowPart: RowPart): Part {
                val children = rowPart.children.map { convertToPart(it) }
                return rowPart.part.copy(
                    children = children,
                    isAssy = children.isNotEmpty() // Ein Teil ist ein Assembly, wenn es Children hat
                )
            }

            val rootParts = allParts.map { convertToPart(it) }

            workbook.close()
            rootParts
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany { partList ->
                partRepository.saveAll(partList)
            }
            .then()
    }
}
