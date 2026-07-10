import { useEffect, useState } from 'react'
import {
  Box, Typography, TextField, Button, Table, TableHead, TableRow, TableCell,
  TableBody, Paper, TableContainer, Stack, Alert, IconButton
} from '@mui/material'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import api from '../api/client'
import { Insumo } from '../types'

export default function InsumosPage() {
  const [insumos, setInsumos] = useState<Insumo[]>([])
  const [nome, setNome] = useState('')
  const [unidade, setUnidade] = useState('')
  const [quantidade, setQuantidade] = useState('0')
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [erro, setErro] = useState<string | null>(null)
  const [carregando, setCarregando] = useState(false)

  const carregar = async () => {
    const resposta = await api.get<Insumo[]>('/insumos')
    setInsumos(resposta.data)
  }

  useEffect(() => {
    carregar()
  }, [])

  const limparForm = () => {
    setNome('')
    setUnidade('')
    setQuantidade('0')
    setEditandoId(null)
  }

  const salvar = async () => {
    setErro(null)
    if (!nome.trim()) {
      setErro('Informe o nome do insumo')
      return
    }
    setCarregando(true)
    try {
      const payload = { nome, unidadeMedida: unidade || null, quantidadeEstoque: Number(quantidade) }
      if (editandoId) {
        await api.put(`/insumos/${editandoId}`, payload)
      } else {
        await api.post('/insumos', payload)
      }
      limparForm()
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Erro ao salvar insumo')
    } finally {
      setCarregando(false)
    }
  }

  const editar = (insumo: Insumo) => {
    setEditandoId(insumo.id)
    setNome(insumo.nome)
    setUnidade(insumo.unidadeMedida || '')
    setQuantidade(String(insumo.quantidadeEstoque))
  }

  const remover = async (id: number) => {
    if (!confirm('Remover este insumo? Essa ação não pode ser desfeita.')) return
    try {
      await api.delete(`/insumos/${id}`)
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Não foi possível remover (verifique se algum produto usa esse insumo)')
    }
  }

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} gutterBottom>
        Insumos
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Cadastre aqui tudo que você recebe da fábrica: toalhas, embalagens, etiquetas, papelões, etc.
      </Typography>

      <Paper sx={{ p: 3, mb: 4 }} variant="outlined">
        <Typography variant="subtitle1" fontWeight={600} gutterBottom>
          {editandoId ? 'Editar insumo' : 'Novo insumo'}
        </Typography>
        {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
          <TextField label="Nome" value={nome} onChange={(e) => setNome(e.target.value)} sx={{ minWidth: 220 }} />
          <TextField label="Unidade (opcional)" value={unidade} onChange={(e) => setUnidade(e.target.value)} sx={{ minWidth: 160 }} placeholder="un, m, kg..." />
          <TextField label="Quantidade em estoque" type="number" value={quantidade} onChange={(e) => setQuantidade(e.target.value)} sx={{ minWidth: 160 }} />
          <Button variant="contained" onClick={salvar} disabled={carregando}>
            {editandoId ? 'Salvar alterações' : 'Adicionar'}
          </Button>
          {editandoId && <Button onClick={limparForm}>Cancelar</Button>}
        </Stack>
      </Paper>

      <TableContainer component={Paper} variant="outlined">
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Unidade</TableCell>
              <TableCell align="right">Estoque atual</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {insumos.map((insumo) => (
              <TableRow key={insumo.id} hover>
                <TableCell>{insumo.nome}</TableCell>
                <TableCell>{insumo.unidadeMedida || '—'}</TableCell>
                <TableCell align="right">{insumo.quantidadeEstoque}</TableCell>
                <TableCell align="right">
                  <IconButton size="small" onClick={() => editar(insumo)}><EditIcon fontSize="small" /></IconButton>
                  <IconButton size="small" onClick={() => remover(insumo.id)}><DeleteIcon fontSize="small" /></IconButton>
                </TableCell>
              </TableRow>
            ))}
            {insumos.length === 0 && (
              <TableRow><TableCell colSpan={4} align="center">Nenhum insumo cadastrado ainda.</TableCell></TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  )
}
