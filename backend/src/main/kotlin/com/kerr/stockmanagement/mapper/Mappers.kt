package com.kerr.stockmanagement.mapper

import com.kerr.stockmanagement.domain.Embalagem
import com.kerr.stockmanagement.domain.Envio
import com.kerr.stockmanagement.domain.Insumo
import com.kerr.stockmanagement.domain.MovimentacaoEstoque
import com.kerr.stockmanagement.domain.Produto
import com.kerr.stockmanagement.dto.EmbalagemResponse
import com.kerr.stockmanagement.dto.EnvioResponse
import com.kerr.stockmanagement.dto.InsumoResponse
import com.kerr.stockmanagement.dto.ItemReceitaResponse
import com.kerr.stockmanagement.dto.MovimentacaoResponse
import com.kerr.stockmanagement.dto.ProdutoResponse

fun Insumo.toDto() = InsumoResponse(
    id = id!!,
    nome = nome,
    unidadeMedida = unidadeMedida,
    quantidadeEstoque = quantidadeEstoque
)

fun Produto.toDto() = ProdutoResponse(
    id = id!!,
    nome = nome,
    descricao = descricao,
    insumoGargaloId = insumoGargalo?.id ?: error("Produto sem insumo gargalo definido"),
    insumoGargaloNome = insumoGargalo?.nome ?: "",
    receita = receita.map { ItemReceitaResponse(it.insumo.id!!, it.insumo.nome, it.quantidadeNecessaria) }
)

fun Embalagem.toDto() = EmbalagemResponse(
    id = id!!,
    produtoId = produto.id!!,
    produtoNome = produto.nome,
    quantidade = quantidade,
    dataEmbalagem = dataEmbalagem,
    enviado = envio != null
)

fun Envio.toDto() = EnvioResponse(
    id = id!!,
    dataEnvio = dataEnvio,
    observacao = observacao,
    itens = itens.map { it.toDto() }
)

fun MovimentacaoEstoque.toDto() = MovimentacaoResponse(
    id = id!!,
    insumoId = insumo.id!!,
    insumoNome = insumo.nome,
    tipo = tipo.name,
    quantidade = quantidade,
    motivo = motivo,
    dataMovimentacao = dataMovimentacao
)
