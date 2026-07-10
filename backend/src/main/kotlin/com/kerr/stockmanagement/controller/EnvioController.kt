package com.kerr.stockmanagement.controller

import com.kerr.stockmanagement.dto.FecharEnvioRequest
import com.kerr.stockmanagement.service.EnvioService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/envios")
class EnvioController(private val service: EnvioService) {

    @PostMapping
    fun fechar(@RequestBody(required = false) request: FecharEnvioRequest?) =
        service.fecharEnvio(request ?: FecharEnvioRequest())

    @GetMapping
    fun listarHistorico() = service.listarHistorico()

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: Long) = service.buscar(id)
}
