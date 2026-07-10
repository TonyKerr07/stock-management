package com.kerr.stockmanagement.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "insumos")
data class Insumo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var nome: String,

    @Column(name = "unidade_medida")
    var unidadeMedida: String? = null,

    @Column(name = "quantidade_estoque", nullable = false)
    var quantidadeEstoque: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)
