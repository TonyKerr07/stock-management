package com.kerr.stockmanagement.controller

import com.kerr.stockmanagement.dto.ProdutoRequest
import com.kerr.stockmanagement.service.ProdutoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/produtos")
class ProdutoController(private val service: ProdutoService) {

    @GetMapping
    fun listar() = service.listar()

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: Long) = service.buscar(id)

    @GetMapping("/{id}/capacidade")
    fun capacidade(@PathVariable id: Long) = service.capacidade(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(@Valid @RequestBody request: ProdutoRequest) = service.criar(request)

    @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Long, @Valid @RequestBody request: ProdutoRequest) = service.atualizar(id, request)

    @DeleteMapping("/{id}")
    fun remover(@PathVariable id: Long): ResponseEntity<Void> {
        service.remover(id)
        return ResponseEntity.noContent().build()
    }
}
