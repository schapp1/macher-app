package com.ts.macher.model

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


    fun processExcelFile(file: MultipartFile, msnId: String): Mono<Void> {
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
            val matShortTextColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "materialkurztext" }
                ?: throw IllegalArgumentException("Keine 'Materialkurztext'-Spalte in der Excel-Datei gefunden")

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
                val matShortText = row.getCell(matShortTextColumnIndex)?.toString()?.trim() ?: ""

                // Erstelle nur ein RowPart, wenn die Bedingungen erfüllt sind
                if ((level != 3 && (idlNumber.isNotBlank() || partNumber.isNotBlank())) ||
                    (level == 3 && partNumber.isNotBlank())
                ) {

                    val rowPart = RowPart(
                        index = i,
                        part = Part(
                            id = UUID.randomUUID(),
                            idlNumber = idlNumber,
                            partNumber = partNumber,
                            level = level.toString(),
                            isAssy = false,
                            matShortText = matShortText,
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
                            // Nur Level-3-Parts mit nicht-leerer Partnummer werden gespeichert
                            allParts.add(rowPart)
                        }
                    }
                }
            }

            // Konvertiere die Hierarchie in Part-Objekte und setze isAssy
            fun convertToPart(rowPart: RowPart): Part {
                val children = rowPart.children.map { childRowPart ->
                    convertToPart(childRowPart).copy(
                        msnIds = (rowPart.part.msnIds + msnId).distinct()
                    )
                }
                return rowPart.part.copy(
                    children = children,
                    isAssy = children.isNotEmpty(),
                    msnIds = (rowPart.part.msnIds + msnId).distinct()
                )
            }

            val rootParts = allParts.map { convertToPart(it) }

            workbook.close()
            rootParts
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany { newParts ->
                // Zuerst alle existierenden Parts laden
                partRepository.findAll()
                    .collectList()
                    .flatMapMany { existingParts ->
                        val partsToUpdate = mutableListOf<Part>()
                        val partsToCreate = mutableListOf<Part>()

                        // Hilfsfunktion zum Aktualisieren der msnIds
                        fun updatePartAndChildren(part: Part): Part {
                            val updatedChildren = part.children.map { child ->
                                updatePartAndChildren(child)
                            }
                            return part.copy(
                                children = updatedChildren,
                                msnIds = (part.msnIds + msnId).distinct()
                            )
                        }

                        newParts.forEach { newPart ->
                            // Suche nach existierendem Part mit gleicher Partnummer
                            val existingPart = existingParts.find { it.partNumber == newPart.partNumber }
                            if (existingPart != null) {
                                // Part existiert bereits, nur msnId hinzufügen
                                partsToUpdate.add(updatePartAndChildren(existingPart))
                            } else {
                                // Neuer Part, komplett hinzufügen
                                partsToCreate.add(updatePartAndChildren(newPart))
                            }
                        }

                        // Erst Updates ausführen, dann neue Parts erstellen
                        Flux.concat(
                            partRepository.saveAll(partsToUpdate),
                            partRepository.saveAll(partsToCreate)
                        )
                    }
            }
            .then()
    }
}
