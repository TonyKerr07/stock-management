package com.kerr.stockmanagement.dto

import java.time.Instant

data class FecharEnvioRequest(
    val observacao: String? = null
)

data class EnvioResponse(
    val id: Long,
    val dataEnvio: Instant,
    val observacao: String?,
    val itens: List<EmbalagemResponse>
)
