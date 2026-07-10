package com.kerr.stockmanagement.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class ItemReceitaRequest(
    val insumoId: Long,
    @field:Min(value = 1, message = "A quantidade necessária deve ser maior que zero")
    val quantidadeNecessaria: Int
)

data class ProdutoRequest(
    @field:NotBlank(message = "O nome do produto é obrigatório")
    val nome: String,
    val descricao: String? = null,
    val insumoGargaloId: Long,
    @field:NotEmpty(message = "O produto precisa ter ao menos um insumo na receita")
    val receita: List<ItemReceitaRequest>
)

data class ItemReceitaResponse(
    val insumoId: Long,
    val insumoNome: String,
    val quantidadeNecessaria: Int
)

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val insumoGargaloId: Long,
    val insumoGargaloNome: String,
    val receita: List<ItemReceitaResponse>
)
