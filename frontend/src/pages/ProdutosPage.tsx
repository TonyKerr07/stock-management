import { useEffect, useState } from 'react'
import {
  Box, Typography, TextField, Button, Paper, Stack, Alert, MenuItem,
  Select, InputLabel, FormControl, IconButton, Radio, RadioGroup,
  FormControlLabel, Table, TableHead, TableRow, TableCell, TableBody,
  TableContainer, Chip
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import DeleteIcon from '@mui/icons-material/Delete'
import api from '../api/client'
import { Insumo, Produto } from '../types'

interface LinhaReceita {
  insumoId: string
  quantidadeNecessaria: string
}

export default function ProdutosPage() {
  const [produtos, setProdutos] = useState<Produto[]>([])
  const [insumos, setInsumos] = useState<Insumo[]>([])
  const [nome, setNome] = useState('')
  const [descricao, setDescricao] = useState('')
  const [linhas, setLinhas] = useState<LinhaReceita[]>([{ insumoId: '', quantidadeNecessaria: '1' }])
  const [gargaloId, setGargaloId] = useState('')
  const [erro, setErro] = useState<string | null>(null)
  const [carregando, setCarregando] = useState(false)

  const carregar = async () => {
    const [resProdutos, resInsumos] = await Promise.all([
      api.get<Produto[]>('/produtos'),
      api.get<Insumo[]>('/insumos')
    ])
    setProdutos(resProdutos.data)
    setInsumos(resInsumos.data)
  }

  useEffect(() => {
    carregar()
  }, [])

  const adicionarLinha = () => setLinhas([...linhas, { insumoId: '', quantidadeNecessaria: '1' }])

  const removerLinha = (index: number) => {
    const insumoRemovido = linhas[index].insumoId
    setLinhas(linhas.filter((_, i) => i !== index))
    if (insumoRemovido === gargaloId) setGargaloId('')
  }

  const atualizarLinha = (index: number, campo: keyof LinhaReceita, valor: string) => {
    const novasLinhas = [...linhas]
    novasLinhas[index] = { ...novasLinhas[index], [campo]: valor }
    setLinhas(novasLinhas)
  }

  const limparForm = () => {
    setNome('')
    setDescricao('')
    setLinhas([{ insumoId: '', quantidadeNecessaria: '1' }])
    setGargaloId('')
  }

  const salvar = async () => {
    setErro(null)
    const receitaValida = linhas.filter((l) => l.insumoId)
    if (!nome.trim()) { setErro('Informe o nome do produto'); return }
    if (receitaValida.length === 0) { setErro('Adicione ao menos um insumo na receita'); return }
    if (!gargaloId) { setErro('Escolha qual insumo é o gargalo deste produto'); return }

    setCarregando(true)
    try {
      await api.post('/produtos', {
        nome,
        descricao: descricao || null,
        insumoGargaloId: Number(gargaloId),
        receita: receitaValida.map((l) => ({
          insumoId: Number(l.insumoId),
          quantidadeNecessaria: Number(l.quantidadeNecessaria)
        }))
      })
      limparForm()
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Erro ao salvar produto')
    } finally {
      setCarregando(false)
    }
  }

  const remover = async (id: number) => {
    if (!confirm('Remover este produto?')) return
    try {
      await api.delete(`/produtos/${id}`)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Não foi possível remover o produto')
    }
  }

  const insumosSelecionaveis = linhas.map((l) => l.insumoId).filter(Boolean)

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} gutterBottom>Produtos</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Cada produto é uma "receita": quais insumos e quantas unidades de cada um são gastos por unidade embalada.
        Escolha também qual desses insumos é o gargalo (o item mais escasso/limitante).
      </Typography>

      <Paper sx={{ p: 3, mb: 4 }} variant="outlined">
        <Typography variant="subtitle1" fontWeight={600} gutterBottom>Novo produto</Typography>
        {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}

        <Stack spacing={2} sx={{ mb: 2 }}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
            <TextField label="Nome do produto" value={nome} onChange={(e) => setNome(e.target.value)} sx={{ minWidth: 240 }} />
            <TextField label="Descrição (opcional)" value={descricao} onChange={(e) => setDescricao(e.target.value)} fullWidth />
          </Stack>
        </Stack>

        <Typography variant="subtitle2" sx={{ mt: 2, mb: 1 }}>Receita (insumos consumidos por unidade)</Typography>
        <RadioGroup value={gargaloId} onChange={(e) => setGargaloId(e.target.value)}>
          {linhas.map((linha, index) => (
            <Stack key={index} direction="row" spacing={2} alignItems="center" sx={{ mb: 1 }}>
              <FormControl sx={{ minWidth: 220 }}>
                <InputLabel>Insumo</InputLabel>
                <Select
                  label="Insumo"
                  value={linha.insumoId}
                  onChange={(e) => atualizarLinha(index, 'insumoId', e.target.value)}
                >
                  {insumos
                    .filter((i) => !insumosSelecionaveis.includes(String(i.id)) || String(i.id) === linha.insumoId)
                    .map((i) => (
                      <MenuItem key={i.id} value={String(i.id)}>{i.nome}</MenuItem>
                    ))}
                </Select>
              </FormControl>
              <TextField
                label="Qtd. por unidade"
                type="number"
                value={linha.quantidadeNecessaria}
                onChange={(e) => atualizarLinha(index, 'quantidadeNecessaria', e.target.value)}
                sx={{ minWidth: 140 }}
              />
              <FormControlLabel
                value={linha.insumoId}
                control={<Radio disabled={!linha.insumoId} />}
                label="É o gargalo"
              />
              <IconButton onClick={() => removerLinha(index)} disabled={linhas.length === 1}>
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Stack>
          ))}
        </RadioGroup>
        <Button startIcon={<AddIcon />} onClick={adicionarLinha} sx={{ mt: 1 }}>Adicionar insumo</Button>

        <Box sx={{ mt: 3 }}>
          <Button variant="contained" onClick={salvar} disabled={carregando}>Salvar produto</Button>
        </Box>
      </Paper>

      <TableContainer component={Paper} variant="outlined">
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Produto</TableCell>
              <TableCell>Receita</TableCell>
              <TableCell>Gargalo</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {produtos.map((produto) => (
              <TableRow key={produto.id} hover>
                <TableCell>{produto.nome}</TableCell>
                <TableCell>
                  {produto.receita.map((item) => (
                    <Chip
                      key={item.insumoId}
                      size="small"
                      sx={{ mr: 0.5, mb: 0.5 }}
                      label={`${item.quantidadeNecessaria}x ${item.insumoNome}`}
                    />
                  ))}
                </TableCell>
                <TableCell>
                  <Chip size="small" color="primary" label={produto.insumoGargaloNome} />
                </TableCell>
                <TableCell align="right">
                  <IconButton size="small" onClick={() => remover(produto.id)}><DeleteIcon fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
            {produtos.length === 0 && (
              <TableRow><TableCell colSpan={4} align="center">Nenhum produto cadastrado ainda.</TableCell></TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
