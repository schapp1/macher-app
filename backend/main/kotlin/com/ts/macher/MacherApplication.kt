package com.ts.macher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class MacherApplication

fun main(args: Array<String>) {
    runApplication<MacherApplication>(*args)
}
