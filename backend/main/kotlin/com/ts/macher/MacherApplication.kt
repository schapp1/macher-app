package com.ts.macher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MacherApplication

fun main(args: Array<String>) {
    runApplication<MacherApplication>(*args)
}
