package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.Produto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProdutoRepository : JpaRepository<Produto, Long> {
    fun findAllByOrderByNomeAsc(): List<Produto>

    @Query("select distinct p from Produto p join p.receita r where r.insumo.id = :insumoId")
    fun findByReceitaInsumoId(@Param("insumoId") insumoId: Long): List<Produto>
}
