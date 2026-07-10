package com.kerr.stockmanagement.dto

import jakarta.validation.constraints.Min

data class EntradaEstoqueRequest(
    val insumoId: Long,
    @field:Min(value = 1, message = "A quantidade de entrada deve ser maior que zero")
    val quantidade: Int,
    val motivo: String? = null
)

data class CapacidadeInsumoResponse(
    val insumoId: Long,
    val insumoNome: String,
    val estoqueAtual: Int,
    val quantidadeNecessariaPorUnidade: Int,
    val capacidadeMaxima: Int,
    val ehGargaloDefinido: Boolean
)

data class CapacidadeProdutoResponse(
    val produtoId: Long,
    val produtoNome: String,
    val capacidadeMaxima: Int,
    val insumoLimitanteId: Long?,
    val insumoLimitanteNome: String?,
    val gargaloConsistente: Boolean,
    val itens: List<CapacidadeInsumoResponse>
)

data class EntradaEstoqueResponse(
    val insumo: InsumoResponse,
    val alertasCapacidade: List<CapacidadeProdutoResponse>
)

data class MovimentacaoResponse(
    val id: Long,
    val insumoId: Long,
    val insumoNome: String,
    val tipo: String,
    val quantidade: Int,
    val motivo: String?,
    val dataMovimentacao: java.time.Instant
)
