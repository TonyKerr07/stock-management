package com.kerr.stockmanagement.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class InsumoRequest(
    @field:NotBlank(message = "O nome do insumo é obrigatório")
    val nome: String,
    val unidadeMedida: String? = null,
    @field:Min(value = 0, message = "A quantidade em estoque não pode ser negativa")
    val quantidadeEstoque: Int = 0
)

data class InsumoResponse(
    val id: Long,
    val nome: String,
    val unidadeMedida: String?,
    val quantidadeEstoque: Int
)
