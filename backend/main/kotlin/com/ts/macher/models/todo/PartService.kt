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

            // Finde die Indizes der relevanten Spalten
            val idlColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "idl-nummer" }
                ?: throw IllegalArgumentException("Keine 'IDL-Nummer'-Spalte in der Excel-Datei gefunden")
            val partNumberColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "partnummer" }
                ?: throw IllegalArgumentException("Keine 'Partnummer'-Spalte in der Excel-Datei gefunden")
            val aufloesungsstufeColumnIndex = (0 until headerRow.physicalNumberOfCells)
                .find { headerRow.getCell(it)?.stringCellValue?.lowercase() == "auflösungsstufe" }
                ?: throw IllegalArgumentException("Keine 'Auflösungsstufe'-Spalte in der Excel-Datei gefunden")

            val parts = mutableListOf<Part>()

            // Ab Zeile 1 (nach der Überschriftenzeile) alle Zeilen durchlaufen
            for (i in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i) ?: continue
                val idlNumber = row.getCell(idlColumnIndex)?.toString()?.trim() ?: continue
                val partNumber = row.getCell(partNumberColumnIndex)?.toString()?.trim() ?: continue
                val level = row.getCell(aufloesungsstufeColumnIndex)
                    ?.toString()
                    ?.trim()
                    ?.replace(Regex("\\D"), "") // entfernt alles außer Ziffern
                    ?: continue
                if (idlNumber.isNotBlank() && partNumber.isNotBlank() && level.isNotBlank()) {
                    parts.add(Part(
                        id = UUID.randomUUID(),
                        idlNumber = idlNumber,
                        partNumber = partNumber,
                        level = level,
                    ))
                }
            }

            workbook.close()
            parts
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMapMany { partList ->
                partRepository.saveAll(partList)
            }
            .then()
    }
}
