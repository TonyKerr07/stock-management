package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Produto
import com.kerr.stockmanagement.dto.CapacidadeInsumoResponse
import com.kerr.stockmanagement.dto.CapacidadeProdutoResponse
import org.springframework.stereotype.Service

/**
 * Serviço responsável pela regra de negócio central do sistema: calcular, para um
 * determinado produto, quantas unidades ainda é possível embalar considerando o
 * estoque atual de cada insumo da receita, e verificar se o insumo definido como
 * "gargalo" pelo usuário é, de fato, o fator limitante no momento.
 *
 * Exemplo: se o produto "Toalha Embalada" precisa de 1 toalha + 1 embalagem + 1 etiqueta,
 * e o estoque atual é 50 toalhas, 40 embalagens e 100 etiquetas, a capacidade máxima é 40
 * (limitada pelas embalagens) — mesmo que o usuário tenha definido a toalha como gargalo.
 */
@Service
class CapacidadeService {

    fun calcularCapacidade(produto: Produto): CapacidadeProdutoResponse {
        val itens = produto.receita.map { item ->
            val capacidade = if (item.quantidadeNecessaria > 0) {
                item.insumo.quantidadeEstoque / item.quantidadeNecessaria
            } else {
                Int.MAX_VALUE
            }
            CapacidadeInsumoResponse(
                insumoId = item.insumo.id!!,
                insumoNome = item.insumo.nome,
                estoqueAtual = item.insumo.quantidadeEstoque,
                quantidadeNecessariaPorUnidade = item.quantidadeNecessaria,
                capacidadeMaxima = capacidade,
                ehGargaloDefinido = produto.insumoGargalo?.id == item.insumo.id
            )
        }

        val insumoLimitante = itens.minByOrNull { it.capacidadeMaxima }

        return CapacidadeProdutoResponse(
            produtoId = produto.id!!,
            produtoNome = produto.nome,
            capacidadeMaxima = insumoLimitante?.capacidadeMaxima ?: 0,
            insumoLimitanteId = insumoLimitante?.insumoId,
            insumoLimitanteNome = insumoLimitante?.insumoNome,
            gargaloConsistente = insumoLimitante?.ehGargaloDefinido ?: true,
            itens = itens
        )
    }
}
