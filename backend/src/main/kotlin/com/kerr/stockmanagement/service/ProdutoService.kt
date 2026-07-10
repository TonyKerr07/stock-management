package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Produto
import com.kerr.stockmanagement.domain.ProdutoInsumo
import com.kerr.stockmanagement.dto.CapacidadeProdutoResponse
import com.kerr.stockmanagement.dto.ProdutoRequest
import com.kerr.stockmanagement.dto.ProdutoResponse
import com.kerr.stockmanagement.mapper.toDto
import com.kerr.stockmanagement.repository.InsumoRepository
import com.kerr.stockmanagement.repository.ProdutoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProdutoService(
    private val produtoRepository: ProdutoRepository,
    private val insumoRepository: InsumoRepository,
    private val capacidadeService: CapacidadeService
) {

    fun listar(): List<ProdutoResponse> = produtoRepository.findAllByOrderByNomeAsc().map { it.toDto() }

    fun buscar(id: Long): ProdutoResponse =
        buscarEntidade(id).toDto()

    fun capacidade(id: Long): CapacidadeProdutoResponse =
        capacidadeService.calcularCapacidade(buscarEntidade(id))

    @Transactional
    fun criar(request: ProdutoRequest): ProdutoResponse {
        val gargalo = insumoRepository.findById(request.insumoGargaloId)
            .orElseThrow { NoSuchElementException("Insumo gargalo ${request.insumoGargaloId} não encontrado") }

        require(request.receita.any { it.insumoId == request.insumoGargaloId }) {
            "O insumo definido como gargalo precisa fazer parte da receita do produto"
        }

        val produto = Produto(nome = request.nome, descricao = request.descricao, insumoGargalo = gargalo)
        montarReceita(produto, request)
        return produtoRepository.save(produto).toDto()
    }

    @Transactional
    fun atualizar(id: Long, request: ProdutoRequest): ProdutoResponse {
        val produto = buscarEntidade(id)
        val gargalo = insumoRepository.findById(request.insumoGargaloId)
            .orElseThrow { NoSuchElementException("Insumo gargalo ${request.insumoGargaloId} não encontrado") }

        require(request.receita.any { it.insumoId == request.insumoGargaloId }) {
            "O insumo definido como gargalo precisa fazer parte da receita do produto"
        }

        produto.nome = request.nome
        produto.descricao = request.descricao
        produto.insumoGargalo = gargalo
        produto.receita.clear()
        montarReceita(produto, request)
        return produtoRepository.save(produto).toDto()
    }

    @Transactional
    fun remover(id: Long) {
        if (!produtoRepository.existsById(id)) throw NoSuchElementException("Produto $id não encontrado")
        produtoRepository.deleteById(id)
    }

    private fun montarReceita(produto: Produto, request: ProdutoRequest) {
        request.receita.forEach { item ->
            val insumo = insumoRepository.findById(item.insumoId)
                .orElseThrow { NoSuchElementException("Insumo ${item.insumoId} não encontrado") }
            produto.receita.add(
                ProdutoInsumo(produto = produto, insumo = insumo, quantidadeNecessaria = item.quantidadeNecessaria)
            )
        }
    }

    private fun buscarEntidade(id: Long): Produto =
        produtoRepository.findById(id).orElseThrow { NoSuchElementException("Produto $id não encontrado") }
}
