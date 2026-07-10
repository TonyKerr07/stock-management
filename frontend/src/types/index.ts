export interface Insumo {
  id: number
  nome: string
  unidadeMedida?: string | null
  quantidadeEstoque: number
}

export interface ItemReceita {
  insumoId: number
  insumoNome: string
  quantidadeNecessaria: number
}

export interface Produto {
  id: number
  nome: string
  descricao?: string | null
  insumoGargaloId: number
  insumoGargaloNome: string
  receita: ItemReceita[]
}

export interface CapacidadeInsumo {
  insumoId: number
  insumoNome: string
  estoqueAtual: number
  quantidadeNecessariaPorUnidade: number
  capacidadeMaxima: number
  ehGargaloDefinido: boolean
}

export interface CapacidadeProduto {
  produtoId: number
  produtoNome: string
  capacidadeMaxima: number
  insumoLimitanteId?: number
  insumoLimitanteNome?: string
  gargaloConsistente: boolean
  itens: CapacidadeInsumo[]
  temAlerta: boolean
}

export interface EntradaEstoqueResponse {
  insumo: Insumo
  capacidadesAfetadas: CapacidadeProduto[]
}

export interface FaltaInsumo {
  insumoId: number
  insumoNome: string
  quantidadeNecessaria: number
  quantidadeDisponivel: number
}

export interface Embalagem {
  id: number
  produtoId: number
  produtoNome: string
  quantidade: number
  dataEmbalagem: string
  enviado: boolean
}

export interface Envio {
  id: number
  dataEnvio: string
  observacao?: string | null
  itens: Embalagem[]
}