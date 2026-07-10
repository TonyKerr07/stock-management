import { ReactNode } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { AppBar, Toolbar, Typography, Tabs, Tab, Container, Box } from '@mui/material'
import Inventory2Icon from '@mui/icons-material/Inventory2'

const rotas = [
  { path: '/dashboard', label: 'Dashboard' },
  { path: '/insumos', label: 'Insumos' },
  { path: '/produtos', label: 'Produtos' },
  { path: '/entrada', label: 'Entrada de Estoque' },
  { path: '/embalagem', label: 'Embalar' }
]

export default function Layout({ children }: { children: ReactNode }) {
  const location = useLocation()
  const navigate = useNavigate()

  const abaAtual = rotas.find((r) => location.pathname.startsWith(r.path))?.path || '/dashboard'

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static" color="primary" elevation={0}>
        <Toolbar>
          <Inventory2Icon sx={{ mr: 1.5 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 600 }}>
            Estoque da Fábrica
          </Typography>
        </Toolbar>
        <Tabs
          value={abaAtual}
          onChange={(_, value) => navigate(value)}
          textColor="inherit"
          indicatorColor="secondary"
          variant="scrollable"
          scrollButtons="auto"
          sx={{ bgcolor: 'primary.dark', px: 1 }}
        >
          {rotas.map((rota) => (
            <Tab key={rota.path} value={rota.path} label={rota.label} sx={{ fontWeight: 500 }} />
          ))}
        </Tabs>
      </AppBar>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  )
}
