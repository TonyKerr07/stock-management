import { useEffect, useState } from 'react'
import { Box, Typography, TextField, Button, Paper, Stack, Alert, MenuItem, Select, InputLabel, FormControl } from '@mui/material'
import api from '../api/client'
import { Insumo, EntradaEstoqueResponse } from '../types'
import AlertaGargalo from '../components/AlertaGargalo'

export default function EntradaEstoquePage() {
  const [insumos, setInsumos] = useState<Insumo[]>([])
  const [insumoId, setInsumoId] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [motivo, setMotivo] = useState('')
  const [erro, setErro] = useState<string | null>(null)
  const [resultado, setResultado] = useState<EntradaEstoqueResponse | null>(null)
  const [carregando, setCarregando] = useState(false)

  const carregarInsumos = async () => {
    const resposta = await api.get<Insumo[]>('/insumos')
    setInsumos(resposta.data)
  }

  useEffect(() => {
    carregarInsumos()
  }, [])

  const registrar = async () => {
    setErro(null)
    setResultado(null)
    if (!insumoId) { setErro('Escolha o insumo que você está trazendo da fábrica'); return }
    if (!quantidade || Number(quantidade) <= 0) { setErro('Informe uma quantidade válida'); return }

    setCarregando(true)
    try {
      const resposta = await api.post<EntradaEstoqueResponse>('/estoque/entrada', {
        insumoId: Number(insumoId),
        quantidade: Number(quantidade),
        motivo: motivo || null
      })
      setResultado(resposta.data)
      setQuantidade('')
      setMotivo('')
      await carregarInsumos()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Erro ao registrar entrada de estoque')
    } finally {
      setCarregando(false)
    }
  }

  return (
      <Box>
        <Typography variant="h5" fontWeight={600} gutterBottom>Entrada de Estoque</Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Use esta tela toda vez que você buscar insumos na fábrica. O sistema já soma ao
          estoque e avisa se algum produto ficará travado por falta de outro insumo.
        </Typography>

        <Paper sx={{ p: 3, mb: 3 }} variant="outlined">
          {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
            <FormControl sx={{ minWidth: 240 }}>
              <InputLabel>Insumo</InputLabel>
              <Select label="Insumo" value={insumoId} onChange={(e) => setInsumoId(e.target.value)}>
                {insumos.map((i) => (
                    <MenuItem key={i.id} value={String(i.id)}>
                      {i.nome} (estoque atual: {i.quantidadeEstoque})
                    </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField label="Quantidade trazida" type="number" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} sx={{ minWidth: 180 }} />
            <TextField label="Observação (opcional)" value={motivo} onChange={(e) => setMotivo(e.target.value)} fullWidth />
            <Button variant="contained" onClick={registrar} disabled={carregando}>Registrar entrada</Button>
          </Stack>
        </Paper>

        {resultado && (
            <Box>
              <Alert severity="success" sx={{ mb: 2 }}>
                Estoque de <strong>{resultado.insumo.nome}</strong> atualizado para{' '}
                <strong>{resultado.insumo.quantidadeEstoque}</strong> unidades.
              </Alert>

              {resultado.capacidadesAfetadas.length > 0 ? (
                  <>
                    <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1 }}>
                      Produtos que usam este insumo ({resultado.capacidadesAfetadas.length}):
                    </Typography>
                    {/* Insumo compartilhado por vários produtos aparece aqui, um card para cada um —
                  os com problema (gargalo inconsistente ou capacidade zerada) ficam em
                  laranja/vermelho, os que estão OK ficam em verde. */}
                    {resultado.capacidadesAfetadas
                        .slice()
                        .sort((a, b) => Number(b.temAlerta) - Number(a.temAlerta))
                        .map((alerta) => (
                            <AlertaGargalo key={alerta.produtoId} alerta={alerta} />
                        ))}
                  </>
              ) : (
                  <Alert severity="info">
                    Nenhum produto cadastrado usa este insumo ainda.
                  </Alert>
              )}
            </Box>
        )}
      </Box>
  )
}