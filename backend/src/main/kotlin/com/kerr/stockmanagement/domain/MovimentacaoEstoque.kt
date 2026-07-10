package com.kerr.stockmanagement.domain

import jakarta.persistence.*
import java.time.Instant

enum class TipoMovimentacao { ENTRADA, SAIDA }

@Entity
@Table(name = "movimentacoes_estoque")
data class MovimentacaoEstoque(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false)
    var insumo: Insumo,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tipo: TipoMovimentacao,

    @Column(nullable = false)
    var quantidade: Int,

    var motivo: String? = null,

    @Column(name = "data_movimentacao", nullable = false)
    var dataMovimentacao: Instant = Instant.now()
)
