package com.kerr.stockmanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockManagementApplication

fun main(args: Array<String>) {
    runApplication<StockManagementApplication>(*args)
}
