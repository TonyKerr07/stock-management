import { Alert, AlertTitle, List, ListItem, ListItemText, Chip, Stack } from '@mui/material'
import { CapacidadeProduto } from '../types'

export default function AlertaGargalo({ alerta }: { alerta: CapacidadeProduto }) {
    const critico = alerta.capacidadeMaxima === 0
    // 'success' quando está tudo certo (gargalo consistente e capacidade > 0),
    // 'warning' quando o gargalo definido não bate com o insumo real, 'error' quando zerou
    const severidade = critico ? 'error' : !alerta.gargaloConsistente ? 'warning' : 'success'

    return (
        <Alert severity={severidade} variant="outlined" sx={{ mb: 2 }}>
            <AlertTitle>
                {alerta.produtoNome} — capacidade atual: {alerta.capacidadeMaxima}{' '}
                {alerta.capacidadeMaxima === 1 ? 'unidade' : 'unidades'}
            </AlertTitle>
            {!alerta.gargaloConsistente ? (
                <span>
          O gargalo definido não é o que está limitando a produção agora. O insumo{' '}
                    <strong>{alerta.insumoLimitanteNome}</strong> é quem está segurando a produção.
        </span>
            ) : (
                <span>Tudo certo — o gargalo definido continua sendo o fator limitante.</span>
            )}
            <List dense>
                {alerta.itens.map((item) => (
                    <ListItem key={item.insumoId} disableGutters>
                        <ListItemText
                            primary={`${item.insumoNome}: ${item.estoqueAtual} em estoque (dá para ${item.capacidadeMaxima} unidades)`}
                        />
                        <Stack direction="row" spacing={1}>
                            {item.ehGargaloDefinido && <Chip label="Gargalo definido" size="small" color="primary" />}
                            {item.insumoId === alerta.insumoLimitanteId && (
                                <Chip label="Limitando agora" size="small" color={severidade} />
                            )}
                        </Stack>
                    </ListItem>
                ))}
            </List>
        </Alert>
    )
}