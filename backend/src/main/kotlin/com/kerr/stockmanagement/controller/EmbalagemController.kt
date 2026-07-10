package com.kerr.stockmanagement.controller

import com.kerr.stockmanagement.dto.EmbalagemRequest
import com.kerr.stockmanagement.service.EmbalagemService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/embalagens")
class EmbalagemController(private val service: EmbalagemService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registrar(@Valid @RequestBody request: EmbalagemRequest) = service.registrar(request)

    @GetMapping("/pendentes")
    fun listarPendentes() = service.listarPendentes()
}
