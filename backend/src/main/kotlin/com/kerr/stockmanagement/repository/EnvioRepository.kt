package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.Envio
import org.springframework.data.jpa.repository.JpaRepository

interface EnvioRepository : JpaRepository<Envio, Long> {
    fun findAllByOrderByDataEnvioDesc(): List<Envio>
}
