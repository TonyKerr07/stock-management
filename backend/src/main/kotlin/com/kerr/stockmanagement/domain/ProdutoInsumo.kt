package com.kerr.stockmanagement.domain

import jakarta.persistence.*

@Entity
@Table(
    name = "produto_insumos",
    uniqueConstraints = [UniqueConstraint(columnNames = ["produto_id", "insumo_id"])]
)
data class ProdutoInsumo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    var produto: Produto? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insumo_id", nullable = false)
    var insumo: Insumo,

    @Column(name = "quantidade_necessaria", nullable = false)
    var quantidadeNecessaria: Int
)
