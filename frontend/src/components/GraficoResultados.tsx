import { Card, CardContent, Typography, Box } from '@mui/material';
import type { EstatisticasDashboardDTO } from '../types';
import GraficoDonut, { type DadoGrafico } from './GraficoDonut';

const MAPA_CORES: Record<string, string> = {
  Aprovadas: '#059669',
  Reprovadas: '#dc2626',
  Empate: '#d97706',
  'Sem Votos': '#94a3b8',
};

interface GraficoResultadosProps {
  estatisticas: EstatisticasDashboardDTO | null;
}

const GraficoResultados = ({ estatisticas }: GraficoResultadosProps) => {
  const dados: DadoGrafico[] = estatisticas
    ? [
        { nome: 'Aprovadas', valor: estatisticas.pautasAprovadas },
        { nome: 'Reprovadas', valor: estatisticas.pautasReprovadas },
        { nome: 'Empate', valor: estatisticas.pautasEmpatadas },
        { nome: 'Sem Votos', valor: estatisticas.pautasSemVotos },
      ].filter((d) => d.valor > 0)
    : [];

  const total = dados.reduce((acc, d) => acc + d.valor, 0);
  const temDados = total > 0;

  return (
    <Card
      sx={{
        height: '100%',
        '&:hover': { transform: 'none', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' },
      }}
    >
      <CardContent sx={{ p: 2.5 }}>
        <Box display="flex" justifyContent="space-between" alignItems="baseline" sx={{ mb: 1 }}>
          <Typography variant="subtitle1" fontWeight={600} color="text.primary">
            Resultado das Votações
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Total: {total}
          </Typography>
        </Box>

        {temDados ? (
          <>
            {}
            <GraficoDonut dados={dados} cores={MAPA_CORES} alturaContainer={240} />
          </>
        ) : (
          <Box display="flex" alignItems="center" justifyContent="center" minHeight={260}>
            <Typography variant="body2" color="text.secondary">
              Nenhuma pauta encerrada para exibir.
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default GraficoResultados;
