package com.kerr.stockmanagement.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "produtos")
data class Produto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var nome: String,

    var descricao: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_gargalo_id")
    var insumoGargalo: Insumo? = null,

    @OneToMany(mappedBy = "produto", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var receita: MutableList<ProdutoInsumo> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
)
