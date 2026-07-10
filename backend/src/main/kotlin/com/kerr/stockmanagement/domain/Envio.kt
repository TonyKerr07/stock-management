package com.kerr.stockmanagement.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "envios")
data class Envio(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "data_envio", nullable = false)
    var dataEnvio: Instant = Instant.now(),

    var observacao: String? = null,

    @OneToMany(mappedBy = "envio", fetch = FetchType.EAGER)
    var itens: MutableList<Embalagem> = mutableListOf()
)
