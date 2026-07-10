package com.kerr.stockmanagement.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "embalagens")
data class Embalagem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    var produto: Produto,

    @Column(nullable = false)
    var quantidade: Int,

    @Column(name = "data_embalagem", nullable = false)
    var dataEmbalagem: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id")
    var envio: Envio? = null
)
