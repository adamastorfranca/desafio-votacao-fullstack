import { Card, CardContent, Typography, Box } from '@mui/material';
import type { EstatisticasDashboardDTO } from '../types';
import GraficoDonut, { type DadoGrafico } from './GraficoDonut';

const CORES = ['#d97706', '#059669', '#6366f1']; 

interface GraficoResumoProps {
  estatisticas: EstatisticasDashboardDTO | null;
}

const GraficoResumo = ({ estatisticas }: GraficoResumoProps) => {
  if (!estatisticas) return null;

  const dados: DadoGrafico[] = [
    { nome: 'Aguardando', valor: estatisticas.pautasAguardando },
    { nome: 'Aberta', valor: estatisticas.pautasAbertas },
    { nome: 'Encerradas', valor: estatisticas.pautasEncerradas },
  ];

  const total = dados.reduce((acc, d) => acc + d.valor, 0);
  const temDados = total > 0;

  const MAPA_CORES: Record<string, string> = {
    Aguardando: CORES[0],
    Abertas: CORES[1],
    Encerradas: CORES[2],
  };

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
            Distribuição de Pautas
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
              Nenhum dado disponível.
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default GraficoResumo;
