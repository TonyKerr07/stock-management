package com.kerr.stockmanagement.exception

import com.kerr.stockmanagement.dto.FaltaInsumoResponse

open class BusinessException(message: String) : RuntimeException(message)

class InsufficientStockException(
    val faltantes: List<FaltaInsumoResponse>
) : BusinessException("Estoque insuficiente para realizar a embalagem")
