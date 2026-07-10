import { useEffect, useState } from 'react'
import {
  Box, Typography, Paper, Table, TableHead, TableRow, TableCell, TableBody,
  TableContainer, Grid, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Alert, Accordion, AccordionSummary, AccordionDetails, Chip
} from '@mui/material'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import api from '../api/client'
import { Insumo, Embalagem, Envio } from '../types'

export default function DashboardPage() {
  const [insumos, setInsumos] = useState<Insumo[]>([])
  const [pendentes, setPendentes] = useState<Embalagem[]>([])
  const [historico, setHistorico] = useState<Envio[]>([])
  const [dialogoAberto, setDialogoAberto] = useState(false)
  const [observacao, setObservacao] = useState('')
  const [erro, setErro] = useState<string | null>(null)

  const carregar = async () => {
    const [resInsumos, resPendentes, resHistorico] = await Promise.all([
      api.get<Insumo[]>('/insumos'),
      api.get<Embalagem[]>('/embalagens/pendentes'),
      api.get<Envio[]>('/envios')
    ])
    setInsumos(resInsumos.data)
    setPendentes(resPendentes.data)
    setHistorico(resHistorico.data)
  }

  useEffect(() => {
    carregar()
  }, [])

  const totalPorProduto = pendentes.reduce<Record<string, number>>((acc, item) => {
    acc[item.produtoNome] = (acc[item.produtoNome] || 0) + item.quantidade
    return acc
  }, {})

  const fecharEnvio = async () => {
    setErro(null)
    try {
      await api.post('/envios', { observacao: observacao || null })
      setDialogoAberto(false)
      setObservacao('')
      await carregar()
    } catch (e: any) {
      setErro(e?.response?.data?.message || 'Erro ao fechar envio')
    }
  }

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} gutterBottom>Dashboard</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Visão geral do que está em estoque em casa e do que já foi embalado esperando para voltar
        para a fábrica da sua sogra.
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1 }}>Estoque atual de insumos</Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Insumo</TableCell>
                  <TableCell align="right">Quantidade</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {insumos.map((i) => (
                  <TableRow key={i.id} hover>
                    <TableCell>{i.nome}</TableCell>
                    <TableCell align="right">{i.quantidadeEstoque}</TableCell>
                  </TableRow>
                ))}
                {insumos.length === 0 && (
                  <TableRow><TableCell colSpan={2} align="center">Nenhum insumo cadastrado.</TableCell></TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Grid>

        <Grid item xs={12} md={6}>
          <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1 }}>
            Produtos embalados aguardando envio
          </Typography>
          <Paper variant="outlined" sx={{ p: 2 }}>
            {Object.keys(totalPorProduto).length === 0 ? (
              <Typography color="text.secondary">Nada pendente de envio no momento.</Typography>
            ) : (
              <Box sx={{ mb: 2 }}>
                {Object.entries(totalPorProduto).map(([produto, total]) => (
                  <Chip key={produto} label={`${produto}: ${total}`} sx={{ mr: 1, mb: 1 }} color="primary" variant="outlined" />
                ))}
              </Box>
            )}
            <Button
              variant="contained"
              color="secondary"
              disabled={pendentes.length === 0}
              onClick={() => setDialogoAberto(true)}
            >
              Marcar como enviado para a fábrica
            </Button>
          </Paper>
        </Grid>
      </Grid>

      <Box sx={{ mt: 4 }}>
        <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1 }}>Histórico de envios</Typography>
        {historico.length === 0 && (
          <Typography color="text.secondary">Nenhum envio realizado ainda.</Typography>
        )}
        {historico.map((envio) => (
          <Accordion key={envio.id} variant="outlined">
            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
              <Typography>
                Envio #{envio.id} — {new Date(envio.dataEnvio).toLocaleString('pt-BR')}
                {envio.observacao ? ` — ${envio.observacao}` : ''}
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Produto</TableCell>
                    <TableCell align="right">Quantidade</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {envio.itens.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>{item.produtoNome}</TableCell>
                      <TableCell align="right">{item.quantidade}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </AccordionDetails>
          </Accordion>
        ))}
      </Box>

      <Dialog open={dialogoAberto} onClose={() => setDialogoAberto(false)} fullWidth maxWidth="sm">
        <DialogTitle>Confirmar envio para a fábrica</DialogTitle>
        <DialogContent>
          {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}
          <Typography sx={{ mb: 2 }}>Você está prestes a enviar:</Typography>
          {Object.entries(totalPorProduto).map(([produto, total]) => (
            <Typography key={produto}>• {total}x {produto}</Typography>
          ))}
          <TextField
            label="Observação (opcional)"
            fullWidth
            multiline
            rows={2}
            sx={{ mt: 3 }}
            value={observacao}
            onChange={(e) => setObservacao(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogoAberto(false)}>Cancelar</Button>
          <Button variant="contained" onClick={fecharEnvio}>Confirmar envio</Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}
