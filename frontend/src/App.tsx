import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import DashboardPage from './pages/DashboardPage'
import InsumosPage from './pages/InsumosPage'
import ProdutosPage from './pages/ProdutosPage'
import EntradaEstoquePage from './pages/EntradaEstoquePage'
import EmbalagemPage from './pages/EmbalagemPage'

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/insumos" element={<InsumosPage />} />
          <Route path="/produtos" element={<ProdutosPage />} />
          <Route path="/entrada" element={<EntradaEstoquePage />} />
          <Route path="/embalagem" element={<EmbalagemPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}

export default App
