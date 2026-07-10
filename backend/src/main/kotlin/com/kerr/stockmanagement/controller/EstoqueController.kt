package com.kerr.stockmanagement.controller

import com.kerr.stockmanagement.dto.EntradaEstoqueRequest
import com.kerr.stockmanagement.service.EstoqueService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/estoque")
class EstoqueController(private val service: EstoqueService) {

    @PostMapping("/entrada")
    fun registrarEntrada(@Valid @RequestBody request: EntradaEstoqueRequest) = service.registrarEntrada(request)

    @GetMapping("/movimentacoes")
    fun listarMovimentacoes() = service.listarMovimentacoes()
}
