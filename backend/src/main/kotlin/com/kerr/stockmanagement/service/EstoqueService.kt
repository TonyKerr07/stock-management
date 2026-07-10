package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.MovimentacaoEstoque
import com.kerr.stockmanagement.domain.TipoMovimentacao
import com.kerr.stockmanagement.dto.EntradaEstoqueRequest
import com.kerr.stockmanagement.dto.EntradaEstoqueResponse
import com.kerr.stockmanagement.dto.MovimentacaoResponse
import com.kerr.stockmanagement.mapper.toDto
import com.kerr.stockmanagement.repository.InsumoRepository
import com.kerr.stockmanagement.repository.MovimentacaoEstoqueRepository
import com.kerr.stockmanagement.repository.ProdutoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Cuida da entrada de insumos no estoque (quando o Antonio traz materiais da fábrica
 * para casa) e, a cada entrada, recalcula a capacidade de produção de todos os produtos
 * que usam aquele insumo — retornando alertas quando o insumo que realmente está
 * limitando a produção é diferente do gargalo definido pelo usuário, ou quando a
 * capacidade está zerada.
 */
@Service
class EstoqueService(
    private val insumoRepository: InsumoRepository,
    private val produtoRepository: ProdutoRepository,
    private val movimentacaoRepository: MovimentacaoEstoqueRepository,
    private val capacidadeService: CapacidadeService
) {

    @Transactional
    fun registrarEntrada(request: EntradaEstoqueRequest): EntradaEstoqueResponse {
        val insumo = insumoRepository.findById(request.insumoId)
            .orElseThrow { NoSuchElementException("Insumo ${request.insumoId} não encontrado") }

        insumo.quantidadeEstoque += request.quantidade
        insumo.updatedAt = Instant.now()
        insumoRepository.save(insumo)

        movimentacaoRepository.save(
            MovimentacaoEstoque(
                insumo = insumo,
                tipo = TipoMovimentacao.ENTRADA,
                quantidade = request.quantidade,
                motivo = request.motivo ?: "Retirada na fábrica"
            )
        )

        val produtosAfetados = produtoRepository.findByReceitaInsumoId(insumo.id!!)
        val alertas = produtosAfetados
            .map { capacidadeService.calcularCapacidade(it) }
            .filter { !it.gargaloConsistente || it.capacidadeMaxima == 0 }

        return EntradaEstoqueResponse(insumo = insumo.toDto(), alertasCapacidade = alertas)
    }

    fun listarMovimentacoes(): List<MovimentacaoResponse> =
        movimentacaoRepository.findAllByOrderByDataMovimentacaoDesc().map { it.toDto() }
}
