package com.kerr.stockmanagement.dto

import jakarta.validation.constraints.Min
import java.time.Instant

data class EmbalagemRequest(
    val produtoId: Long,
    @field:Min(value = 1, message = "A quantidade embalada deve ser maior que zero")
    val quantidade: Int
)

data class FaltaInsumoResponse(
    val insumoId: Long,
    val insumoNome: String,
    val quantidadeNecessaria: Int,
    val quantidadeDisponivel: Int
)

data class EmbalagemResponse(
    val id: Long,
    val produtoId: Long,
    val produtoNome: String,
    val quantidade: Int,
    val dataEmbalagem: Instant,
    val enviado: Boolean
)
