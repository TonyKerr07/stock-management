import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import AlertaGargalo from '../components/AlertaGargalo'
import { CapacidadeProduto } from '../types'

describe('AlertaGargalo', () => {
  it('exibe o insumo limitante quando o gargalo definido não é consistente', () => {
    const alerta: CapacidadeProduto = {
      produtoId: 1,
      produtoNome: 'Toalha Embalada',
      capacidadeMaxima: 40,
      insumoLimitanteId: 2,
      insumoLimitanteNome: 'Embalagem',
      gargaloConsistente: false,
      temAlerta: true,
      itens: [
        { insumoId: 1, insumoNome: 'Toalha', estoqueAtual: 50, quantidadeNecessariaPorUnidade: 1, capacidadeMaxima: 50, ehGargaloDefinido: true },
        { insumoId: 2, insumoNome: 'Embalagem', estoqueAtual: 40, quantidadeNecessariaPorUnidade: 1, capacidadeMaxima: 40, ehGargaloDefinido: false }
      ]
    }

    render(<AlertaGargalo alerta={alerta} />)

    expect(screen.getByText(/capacidade atual: 40/i)).toBeInTheDocument()
    expect(screen.getAllByText(/Embalagem/i).length).toBeGreaterThan(0)
    expect(screen.getByText('Limitando agora')).toBeInTheDocument()
    expect(screen.getByText('Gargalo definido')).toBeInTheDocument()
  })

  it('não exibe aviso de inconsistência quando o gargalo definido é o limitante', () => {
    const alerta: CapacidadeProduto = {
      produtoId: 2,
      produtoNome: 'Pêndulo Embalado',
      capacidadeMaxima: 20,
      insumoLimitanteId: 1,
      insumoLimitanteNome: 'Pêndulo',
      gargaloConsistente: true,
      temAlerta: false,
      itens: [
        { insumoId: 1, insumoNome: 'Pêndulo', estoqueAtual: 20, quantidadeNecessariaPorUnidade: 1, capacidadeMaxima: 20, ehGargaloDefinido: true }
      ]
    }

    render(<AlertaGargalo alerta={alerta} />)

    expect(screen.queryByText(/não é o que está limitando/i)).not.toBeInTheDocument()
  })
})