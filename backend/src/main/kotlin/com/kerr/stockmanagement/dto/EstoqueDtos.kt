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
) {
    // true quando o gargalo definido não bate com o insumo que realmente limita a produção,
    // ou quando a capacidade chegou a zero — é o gatilho pra destacar esse produto na tela
    val temAlerta: Boolean
        get() = !gargaloConsistente || capacidadeMaxima == 0
}

data class EntradaEstoqueResponse(
    val insumo: InsumoResponse,
    // Todos os produtos que usam esse insumo na receita (não só os com problema),
    // para o usuário ver o panorama completo quando um insumo é compartilhado
    // entre vários produtos.
    val capacidadesAfetadas: List<CapacidadeProdutoResponse>
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