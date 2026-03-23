import {
  Box,
  Dialog,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  Typography,
  Paper,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import BallotIcon from '@mui/icons-material/Ballot';
import type { PautaDetalhadaRespostaDTO } from '../types';
import BadgeCorporativo from './BadgeCorporativo';
import GraficoDonut from './GraficoDonut';
import { formatarDataHora } from '../utils/formatadores';

const COR_SIM = '#2e7d32';
const COR_NAO = '#c62828';

interface ModalResultadoProps {

  aberto: boolean;

  aoFechar: () => void;

  pauta: PautaDetalhadaRespostaDTO | null;
}

const chipOpcaoGanhadora = (opcao: string | null) => {
  if (!opcao) return null;

  const ehAprovada = opcao.toUpperCase() === 'SIM';
  const ehEmpate = opcao.toUpperCase() === 'EMPATE';

  if (ehEmpate) {
    return (
      <BadgeCorporativo
        tipo="alerta"
        icon={<BallotIcon fontSize="small" />}
        label="Empate"
      />
    );
  }

  return (
    <BadgeCorporativo
      tipo={ehAprovada ? 'sucesso' : 'erro'}
      icon={ehAprovada ? <ThumbUpAltIcon fontSize="small" /> : <ThumbDownAltIcon fontSize="small" />}
      label={ehAprovada ? 'Aprovada' : 'Rejeitada'}
    />
  );
};



const calcularTempoSessao = (inicio: string | null, termino: string | null) => {
  if (!inicio || !termino) return '--';
  const numInicio = new Date(inicio).getTime();
  const numTermino = new Date(termino).getTime();
  const diffMinutos = Math.round((numTermino - numInicio) / 60000);
  if (diffMinutos < 60) return `${diffMinutos} min`;
  const horas = Math.floor(diffMinutos / 60);
  const mins = diffMinutos % 60;
  return `${horas}h ${mins}m`;
};

const ModalResultado = ({ aberto, aoFechar, pauta }: ModalResultadoProps) => {
  if (!pauta) return null;

  const totalVotos = pauta.totalVotos ?? 0;
  const totalSim = pauta.totalSim ?? 0;
  const totalNao = pauta.totalNao ?? 0;

  const dadosGrafico = [
    { nome: 'SIM', valor: totalSim },
    { nome: 'NÃO', valor: totalNao },
  ];

  const coresGrafico: Record<string, string> = {
    'SIM': COR_SIM,
    'NÃO': COR_NAO,
  };

  return (
    <Dialog
      open={aberto}
      onClose={aoFechar}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          overflow: 'hidden',
        },
      }}
    >
      {}
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          pb: 1,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <EmojiEventsIcon color="primary" />
          <Typography variant="h6" fontWeight={700}>
            Resultado da Votação
          </Typography>
        </Box>
        <IconButton onClick={aoFechar} size="small" aria-label="fechar modal">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <Divider />

      {}
      <DialogContent sx={{ pt: 3, pb: 4 }}>
        {}
        <Box sx={{ mb: 3 }}>
          <Typography variant="h5" fontWeight={700} color="text.primary" sx={{ mb: 1, lineHeight: 1.3 }}>
            {pauta.titulo}
          </Typography>
          {pauta.descricao && (
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2, fontSize: '0.9rem' }}>
              {pauta.descricao}
            </Typography>
          )}

          <Box sx={{ display: 'flex', gap: 4, flexWrap: 'wrap', mb: 3 }}>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600} mb={0.5}>
                Situação
              </Typography>
              <BadgeCorporativo tipo="info" label="Sessão Encerrada" />
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600} mb={0.5}>
                Resultado
              </Typography>
              {chipOpcaoGanhadora(pauta.opcaoGanhadora)}
            </Box>
          </Box>

          {}
          <Paper elevation={0} sx={{ p: '12px 16px', bgcolor: '#f8fafc', borderRadius: 2, display: 'flex', flexWrap: 'wrap', gap: 4, border: '1px solid #f1f5f9' }}>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600}>
                Criação da Pauta
              </Typography>
              <Typography variant="body2" fontWeight={600} color="text.primary">
                {formatarDataHora(pauta.dataHoraCriacao)}
              </Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600}>
                Início Sessão
              </Typography>
              <Typography variant="body2" fontWeight={600} color="text.primary">
                {formatarDataHora(pauta.dataHoraInicio)}
              </Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600}>
                Fim Sessão
              </Typography>
              <Typography variant="body2" fontWeight={600} color="text.primary">
                {formatarDataHora(pauta.dataHoraTermino)}
              </Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block" fontWeight={600}>
                Tempo de Sessão
              </Typography>
              <Typography variant="body2" fontWeight={600} color="text.primary">
                {calcularTempoSessao(pauta.dataHoraInicio, pauta.dataHoraTermino)}
              </Typography>
            </Box>
          </Paper>
        </Box>

        <Divider sx={{ mb: 4 }} />

        {}
        {totalVotos > 0 ? (
          <Box display="flex" flexDirection={{ xs: 'column', sm: 'row' }} gap={4} alignItems="center">
            {}
            <Box flex={{ xs: '1 1 auto', sm: '0 0 55%' }} width="100%">
              <GraficoDonut
                dados={dadosGrafico}
                cores={coresGrafico}
                alturaContainer={260}
              />
            </Box>

            {}
            <Box flex={{ xs: '1 1 auto', sm: '0 0 40%' }} width="100%" sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Paper
                elevation={0}
                sx={{
                  p: 2,
                  bgcolor: 'rgba(46, 125, 50, 0.04)',
                  border: '1px solid rgba(46, 125, 50, 0.15)',
                  borderRadius: 3,
                  textAlign: 'center',
                }}
              >
                <Typography variant="h3" fontWeight={800} sx={{ color: COR_SIM, lineHeight: 1, mb: 0.5 }}>
                  {totalSim}
                </Typography>
                <Typography variant="caption" fontWeight={700} sx={{ color: COR_SIM, textTransform: 'uppercase', letterSpacing: 0.5 }}>
                  Votos Sim
                </Typography>
              </Paper>

              <Paper
                elevation={0}
                sx={{
                  p: 2,
                  bgcolor: 'rgba(198, 40, 40, 0.04)',
                  border: '1px solid rgba(198, 40, 40, 0.15)',
                  borderRadius: 3,
                  textAlign: 'center',
                }}
              >
                <Typography variant="h3" fontWeight={800} sx={{ color: COR_NAO, lineHeight: 1, mb: 0.5 }}>
                  {totalNao}
                </Typography>
                <Typography variant="caption" fontWeight={700} sx={{ color: COR_NAO, textTransform: 'uppercase', letterSpacing: 0.5 }}>
                  Votos Não
                </Typography>
              </Paper>

              <Paper
                elevation={0}
                sx={{
                  p: 1.5,
                  bgcolor: '#f8fafc',
                  border: '1px solid #e2e8f0',
                  borderRadius: 3,
                  textAlign: 'center',
                }}
              >
                <Typography variant="h5" fontWeight={700} color="text.primary" sx={{ lineHeight: 1, mb: 0.5 }}>
                  {totalVotos}
                </Typography>
                <Typography variant="caption" fontWeight={600} color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: 0.5 }}>
                  Total de Votos
                </Typography>
              </Paper>
            </Box>
          </Box>
        ) : (

          <Paper
            elevation={0}
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              py: 6,
              bgcolor: '#f8fafc',
              border: '1px dashed #cbd5e1',
              borderRadius: 3,
            }}
          >
            <BallotIcon sx={{ fontSize: 56, color: '#94a3b8', mb: 2 }} />
            <Typography variant="subtitle1" color="text.primary" fontWeight={700} gutterBottom>
              Sessão encerrada sem votos
            </Typography>
            <Typography variant="body2" color="text.secondary" align="center" sx={{ maxWidth: 300 }}>
              Nenhum associado registou o seu voto durante o período em que esta pauta esteve aberta.
            </Typography>
          </Paper>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default ModalResultado;
