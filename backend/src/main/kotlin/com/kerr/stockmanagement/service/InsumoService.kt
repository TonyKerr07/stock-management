package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Insumo
import com.kerr.stockmanagement.dto.InsumoRequest
import com.kerr.stockmanagement.dto.InsumoResponse
import com.kerr.stockmanagement.mapper.toDto
import com.kerr.stockmanagement.repository.InsumoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class InsumoService(private val repository: InsumoRepository) {

    fun listar(): List<InsumoResponse> = repository.findAllByOrderByNomeAsc().map { it.toDto() }

    fun buscar(id: Long): InsumoResponse =
        repository.findById(id).orElseThrow { NoSuchElementException("Insumo $id não encontrado") }.toDto()

    @Transactional
    fun criar(request: InsumoRequest): InsumoResponse {
        val insumo = Insumo(
            nome = request.nome,
            unidadeMedida = request.unidadeMedida,
            quantidadeEstoque = request.quantidadeEstoque
        )
        return repository.save(insumo).toDto()
    }

    @Transactional
    fun atualizar(id: Long, request: InsumoRequest): InsumoResponse {
        val insumo = repository.findById(id).orElseThrow { NoSuchElementException("Insumo $id não encontrado") }
        insumo.nome = request.nome
        insumo.unidadeMedida = request.unidadeMedida
        insumo.quantidadeEstoque = request.quantidadeEstoque
        insumo.updatedAt = Instant.now()
        return repository.save(insumo).toDto()
    }

    @Transactional
    fun remover(id: Long) {
        if (!repository.existsById(id)) throw NoSuchElementException("Insumo $id não encontrado")
        repository.deleteById(id)
    }
}
