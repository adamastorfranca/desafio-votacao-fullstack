import { Card, CardContent, Grid, Typography, Box } from '@mui/material';
import BallotIcon from '@mui/icons-material/Ballot';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import BalanceIcon from '@mui/icons-material/Balance';
import DoNotDisturbAltIcon from '@mui/icons-material/DoNotDisturbAlt';
import type { EstatisticasDashboardDTO } from '../types';

interface CardsResumoProps {
  estatisticas: EstatisticasDashboardDTO | null;
  secao: 'status' | 'resultado';
}

interface ConfigCard {
  titulo: string;
  campo: keyof EstatisticasDashboardDTO;
  icone: React.ReactNode;
  cor: string;
  corFundo: string;
}

const configCardsStatus: ConfigCard[] = [
  {
    titulo: 'Total de Pautas',
    campo: 'totalPautas',
    icone: <BallotIcon />,
    cor: '#475569',     
    corFundo: '#f1f5f9', 
  },
  {
    titulo: 'Aguardando',
    campo: 'pautasAguardando',
    icone: <HourglassEmptyIcon />,
    cor: '#92400e',     
    corFundo: '#fef3c7', 
  },
  {
    titulo: 'Abertas',
    campo: 'pautasAbertas',
    icone: <HowToVoteIcon />,
    cor: '#166534',     
    corFundo: '#dcfce7', 
  },
  {
    titulo: 'Encerradas',
    campo: 'pautasEncerradas',
    icone: <CheckCircleOutlineIcon />,
    cor: '#4338ca',     
    corFundo: '#e0e7ff', 
  },
];

const configCardsResultado: ConfigCard[] = [
  {
    titulo: 'Aprovadas',
    campo: 'pautasAprovadas',
    icone: <ThumbUpAltIcon />,
    cor: '#166534',     
    corFundo: '#dcfce7', 
  },
  {
    titulo: 'Reprovadas',
    campo: 'pautasReprovadas',
    icone: <ThumbDownAltIcon />,
    cor: '#991b1b',     
    corFundo: '#fee2e2', 
  },
  {
    titulo: 'Empate',
    campo: 'pautasEmpatadas',
    icone: <BalanceIcon />,
    cor: '#92400e',     
    corFundo: '#fef3c7', 
  },
  {
    titulo: 'Sem Votos',
    campo: 'pautasSemVotos',
    icone: <DoNotDisturbAltIcon />,
    cor: '#64748b',     
    corFundo: '#f1f5f9', 
  },
];

const CardEstatistica = ({
  titulo,
  valor,
  icone,
  cor,
  corFundo,
}: {
  titulo: string;
  valor: string | number;
  icone: React.ReactNode;
  cor: string;
  corFundo: string;
}) => (
  <Card
    sx={{
      height: '100%',
      '&:hover': { transform: 'none', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' },
    }}
  >
    <CardContent sx={{ p: 2.5, '&:last-child': { pb: 2.5 }, minHeight: 88 }}>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mb: 0.5, fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}
          >
            {titulo}
          </Typography>
          <Typography variant="h4" fontWeight={700} sx={{ color: cor }}>
            {valor}
          </Typography>
        </Box>
        <Box
          sx={{
            width: 48,
            height: 48,
            borderRadius: 2,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            bgcolor: corFundo,
            color: cor,
          }}
        >
          {icone}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

const CardsResumo = ({ estatisticas, secao }: CardsResumoProps) => {
  const cards = secao === 'resultado' ? configCardsResultado : configCardsStatus;

  return (
    <Grid container spacing={2}>
      {cards.map((config) => (
        <Grid size={{ xs: 6, md: 3 }} key={config.campo}>
          <CardEstatistica
            titulo={config.titulo}
            valor={estatisticas ? estatisticas[config.campo] : '—'}
            icone={config.icone}
            cor={config.cor}
            corFundo={config.corFundo}
          />
        </Grid>
      ))}
    </Grid>
  );
};

export default CardsResumo;
