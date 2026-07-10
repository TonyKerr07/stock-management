package com.kerr.stockmanagement.service

import com.kerr.stockmanagement.domain.Insumo
import com.kerr.stockmanagement.domain.Produto
import com.kerr.stockmanagement.domain.ProdutoInsumo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Testa a regra de negócio mais importante do sistema: identificar qual insumo
 * está realmente limitando a produção, mesmo quando o usuário definiu outro
 * insumo como "gargalo" no cadastro do produto.
 */
class CapacidadeServiceTest : FunSpec({

    val service = CapacidadeService()

    test("quando um insumo secundário tem menos estoque relativo que o gargalo definido, ele deve virar o limitante") {
        val toalha = Insumo(id = 1, nome = "Toalha", quantidadeEstoque = 50)
        val embalagem = Insumo(id = 2, nome = "Embalagem", quantidadeEstoque = 40)
        val etiqueta = Insumo(id = 3, nome = "Etiqueta", quantidadeEstoque = 100)

        val produto = Produto(id = 1, nome = "Toalha Embalada", insumoGargalo = toalha)
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = toalha, quantidadeNecessaria = 1))
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = embalagem, quantidadeNecessaria = 1))
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = etiqueta, quantidadeNecessaria = 1))

        val resultado = service.calcularCapacidade(produto)

        resultado.capacidadeMaxima shouldBe 40
        resultado.insumoLimitanteNome shouldBe "Embalagem"
        resultado.gargaloConsistente shouldBe false
    }

    test("quando o gargalo definido é realmente o fator limitante, gargaloConsistente deve ser true") {
        val pendulo = Insumo(id = 1, nome = "Pêndulo", quantidadeEstoque = 20)
        val bolsa = Insumo(id = 2, nome = "Bolsa do pêndulo", quantidadeEstoque = 50)
        val etiqueta = Insumo(id = 3, nome = "Etiqueta", quantidadeEstoque = 80)

        val produto = Produto(id = 2, nome = "Pêndulo Embalado", insumoGargalo = pendulo)
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = pendulo, quantidadeNecessaria = 1))
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = bolsa, quantidadeNecessaria = 1))
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = etiqueta, quantidadeNecessaria = 1))

        val resultado = service.calcularCapacidade(produto)

        resultado.capacidadeMaxima shouldBe 20
        resultado.insumoLimitanteNome shouldBe "Pêndulo"
        resultado.gargaloConsistente shouldBe true
    }

    test("insumos com quantidade necessária por unidade maior que 1 devem ser considerados na divisão") {
        val papelao = Insumo(id = 1, nome = "Papelão", quantidadeEstoque = 30)
        val fita = Insumo(id = 2, nome = "Fita adesiva", quantidadeEstoque = 10)

        val produto = Produto(id = 3, nome = "Caixa Reforçada", insumoGargalo = papelao)
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = papelao, quantidadeNecessaria = 1))
        // cada caixa usa 2 unidades de fita
        produto.receita.add(ProdutoInsumo(produto = produto, insumo = fita, quantidadeNecessaria = 2))

        val resultado = service.calcularCapacidade(produto)

        // 10 / 2 = 5, que é menor que 30 do papelão
        resultado.capacidadeMaxima shouldBe 5
        resultado.insumoLimitanteNome shouldBe "Fita adesiva"
        resultado.gargaloConsistente shouldBe false
    }
})
