package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.MovimentacaoEstoque
import org.springframework.data.jpa.repository.JpaRepository

interface MovimentacaoEstoqueRepository : JpaRepository<MovimentacaoEstoque, Long> {
    fun findAllByOrderByDataMovimentacaoDesc(): List<MovimentacaoEstoque>
    fun findByInsumoIdOrderByDataMovimentacaoDesc(insumoId: Long): List<MovimentacaoEstoque>
}
