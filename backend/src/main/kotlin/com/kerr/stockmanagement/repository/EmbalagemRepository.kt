package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.Embalagem
import org.springframework.data.jpa.repository.JpaRepository

interface EmbalagemRepository : JpaRepository<Embalagem, Long> {
    fun findByEnvioIsNullOrderByDataEmbalagemDesc(): List<Embalagem>
}
