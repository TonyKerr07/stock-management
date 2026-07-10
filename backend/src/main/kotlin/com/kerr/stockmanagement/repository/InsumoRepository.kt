package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.Insumo
import org.springframework.data.jpa.repository.JpaRepository

interface InsumoRepository : JpaRepository<Insumo, Long> {
    fun findAllByOrderByNomeAsc(): List<Insumo>
}
