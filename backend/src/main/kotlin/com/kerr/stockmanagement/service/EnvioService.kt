package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Envio
import com.kerr.stockmanagement.dto.EnvioResponse
import com.kerr.stockmanagement.dto.FecharEnvioRequest
import com.kerr.stockmanagement.exception.BusinessException
import com.kerr.stockmanagement.mapper.toDto
import com.kerr.stockmanagement.repository.EmbalagemRepository
import com.kerr.stockmanagement.repository.EnvioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Fecha um "envio": pega todas as embalagens ainda não enviadas de volta para a
 * fábrica da sogra, agrupa em um novo Envio (zerando a contagem de "pendentes" na
 * tela principal) e preserva o histórico, já que cada Embalagem passa a apontar
 * para o Envio ao qual pertenceu.
 */
@Service
class EnvioService(
    private val envioRepository: EnvioRepository,
    private val embalagemRepository: EmbalagemRepository
) {

    @Transactional
    fun fecharEnvio(request: FecharEnvioRequest): EnvioResponse {
        val pendentes = embalagemRepository.findByEnvioIsNullOrderByDataEmbalagemDesc()
        if (pendentes.isEmpty()) {
            throw BusinessException("Não há embalagens pendentes para enviar")
        }

        val envio = envioRepository.save(Envio(observacao = request.observacao))
        pendentes.forEach { embalagem ->
            embalagem.envio = envio
            embalagemRepository.save(embalagem)
        }
        envio.itens = pendentes.toMutableList()
        return envio.toDto()
    }

    fun listarHistorico(): List<EnvioResponse> =
        envioRepository.findAllByOrderByDataEnvioDesc().map { it.toDto() }

    fun buscar(id: Long): EnvioResponse =
        envioRepository.findById(id).orElseThrow { NoSuchElementException("Envio $id não encontrado") }.toDto()
}
