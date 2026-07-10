package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Embalagem
import com.kerr.stockmanagement.domain.MovimentacaoEstoque
import com.kerr.stockmanagement.domain.TipoMovimentacao
import com.kerr.stockmanagement.dto.EmbalagemRequest
import com.kerr.stockmanagement.dto.EmbalagemResponse
import com.kerr.stockmanagement.dto.FaltaInsumoResponse
import com.kerr.stockmanagement.exception.InsufficientStockException
import com.kerr.stockmanagement.mapper.toDto
import com.kerr.stockmanagement.repository.EmbalagemRepository
import com.kerr.stockmanagement.repository.InsumoRepository
import com.kerr.stockmanagement.repository.MovimentacaoEstoqueRepository
import com.kerr.stockmanagement.repository.ProdutoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Registra a embalagem de produtos: valida se há estoque suficiente de TODOS os
 * insumos da receita para a quantidade informada e, em caso positivo, desconta
 * automaticamente cada insumo utilizado. Em caso negativo, lança uma exceção com
 * o detalhamento de quais insumos estão faltando e quanto falta de cada um.
 */
@Service
class EmbalagemService(
    private val produtoRepository: ProdutoRepository,
    private val insumoRepository: InsumoRepository,
    private val embalagemRepository: EmbalagemRepository,
    private val movimentacaoRepository: MovimentacaoEstoqueRepository
) {

    @Transactional
    fun registrar(request: EmbalagemRequest): EmbalagemResponse {
        val produto = produtoRepository.findById(request.produtoId)
            .orElseThrow { NoSuchElementException("Produto ${request.produtoId} não encontrado") }

        val faltantes = produto.receita.mapNotNull { item ->
            val necessario = item.quantidadeNecessaria * request.quantidade
            if (item.insumo.quantidadeEstoque < necessario) {
                FaltaInsumoResponse(
                    insumoId = item.insumo.id!!,
                    insumoNome = item.insumo.nome,
                    quantidadeNecessaria = necessario,
                    quantidadeDisponivel = item.insumo.quantidadeEstoque
                )
            } else null
        }

        if (faltantes.isNotEmpty()) {
            throw InsufficientStockException(faltantes)
        }

        produto.receita.forEach { item ->
            val necessario = item.quantidadeNecessaria * request.quantidade
            item.insumo.quantidadeEstoque -= necessario
            item.insumo.updatedAt = Instant.now()
            insumoRepository.save(item.insumo)

            movimentacaoRepository.save(
                MovimentacaoEstoque(
                    insumo = item.insumo,
                    tipo = TipoMovimentacao.SAIDA,
                    quantidade = necessario,
                    motivo = "Embalagem de ${produto.nome}"
                )
            )
        }

        val embalagem = embalagemRepository.save(Embalagem(produto = produto, quantidade = request.quantidade))
        return embalagem.toDto()
    }

    fun listarPendentes(): List<EmbalagemResponse> =
        embalagemRepository.findByEnvioIsNullOrderByDataEmbalagemDesc().map { it.toDto() }
}
