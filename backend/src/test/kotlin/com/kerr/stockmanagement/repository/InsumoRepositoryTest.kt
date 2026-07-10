package com.kerr.stockmanagement.repository

import com.kerr.stockmanagement.domain.Insumo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@DataJpaTest
@ActiveProfiles("test")
class InsumoRepositoryTest @Autowired constructor(
    private val repository: InsumoRepository
) {

    @Test
    fun `deve salvar e listar insumos ordenados por nome`() {
        repository.save(Insumo(nome = "Zíper", quantidadeEstoque = 10))
        repository.save(Insumo(nome = "Algodão", quantidadeEstoque = 5))

        val resultado = repository.findAllByOrderByNomeAsc()

        assertEquals(2, resultado.size)
        assertEquals("Algodão", resultado.first().nome)
        assertEquals("Zíper", resultado.last().nome)
    }
}
