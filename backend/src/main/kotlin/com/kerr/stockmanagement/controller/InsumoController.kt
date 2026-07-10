package com.kerr.stockmanagement.controller

import com.kerr.stockmanagement.dto.InsumoRequest
import com.kerr.stockmanagement.service.InsumoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/insumos")
class InsumoController(private val service: InsumoService) {

    @GetMapping
    fun listar() = service.listar()

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: Long) = service.buscar(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(@Valid @RequestBody request: InsumoRequest) = service.criar(request)

    @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Long, @Valid @RequestBody request: InsumoRequest) = service.atualizar(id, request)

    @DeleteMapping("/{id}")
    fun remover(@PathVariable id: Long): ResponseEntity<Void> {
        service.remover(id)
        return ResponseEntity.noContent().build()
    }
}
