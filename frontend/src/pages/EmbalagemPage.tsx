import { useEffect, useState } from 'react'
import {
  Box, Typography, TextField, Button, Paper, Stack, Alert, MenuItem, Select,
  InputLabel, FormControl, List, ListItem, ListItemText
} from '@mui/material'
import api from '../api/client'
import { Produto, FaltaInsumo, Embalagem } from '../types'

export default function EmbalagemPage() {
  const [produtos, setProdutos] = useState<Produto[]>([])
  const [produtoId, setProdutoId] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [erro, setErro] = useState<string | null>(null)
  const [faltantes, setFaltantes] = useState<FaltaInsumo[]>([])
  const [sucesso, setSucesso] = useState<Embalagem | null>(null)
  const [carregando, setCarregando] = useState(false)

  useEffect(() => {
    api.get<Produto[]>('/produtos').then((res) => setProdutos(res.data))
  }, [])

  const registrar = async () => {
    setErro(null)
    setFaltantes([])
    setSucesso(null)
    if (!produtoId) { setErro('Escolha o produto que você embalou'); return }
    if (!quantidade || Number(quantidade) <= 0) { setErro('Informe uma quantidade válida'); return }

    setCarregando(true)
    try {
      const resposta = await api.post<Embalagem>('/embalagens', {
        produtoId: Number(produtoId),
        quantidade: Number(quantidade)
      })
      setSucesso(resposta.data)
      setQuantidade('')
    } catch (e: any) {
      if (e?.response?.status === 409) {
        setErro(e.response.data.message)
        setFaltantes(e.response.data.faltantes || [])
      } else {
        setErro(e?.response?.data?.message || 'Erro ao registrar embalagem')
      }
    } finally {
      setCarregando(false)
    }
  }

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} gutterBottom>Embalar produto</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Informe quantas unidades foram embaladas. O sistema desconta automaticamente cada insumo
        da receita — e avisa se não houver estoque suficiente para completar a embalagem.
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }} variant="outlined">
        {erro && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {erro}
            {faltantes.length > 0 && (
              <List dense>
                {faltantes.map((f) => (
                  <ListItem key={f.insumoId} disableGutters>
                    <ListItemText
                      primary={`${f.insumoNome}: precisa de ${f.quantidadeNecessaria}, tem apenas ${f.quantidadeDisponivel}`}
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </Alert>
        )}
        {sucesso && (
          <Alert severity="success" sx={{ mb: 2 }}>
            {sucesso.quantidade} unidade(s) de <strong>{sucesso.produtoNome}</strong> embalada(s) com sucesso!
            O estoque dos insumos já foi descontado.
          </Alert>
        )}

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
          <FormControl sx={{ minWidth: 240 }}>
            <InputLabel>Produto</InputLabel>
            <Select label="Produto" value={produtoId} onChange={(e) => setProdutoId(e.target.value)}>
              {produtos.map((p) => (
                <MenuItem key={p.id} value={String(p.id)}>{p.nome}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField label="Quantidade embalada" type="number" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} sx={{ minWidth: 180 }} />
          <Button variant="contained" onClick={registrar} disabled={carregando}>Registrar embalagem</Button>
        </Stack>
      </Paper>
    </Box>
  )
}
