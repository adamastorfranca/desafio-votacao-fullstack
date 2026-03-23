import { useState } from 'react';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField,
  InputAdornment,
  Box,
  Typography,
} from '@mui/material';
import { toast } from 'react-toastify';

import { abrirSessao } from '../services/sessaoService';
import type { PautaDetalhadaRespostaDTO } from '../types';
import { formatarDataHora } from '../utils/formatadores';



interface ModalAbrirSessaoProps {

  pauta: PautaDetalhadaRespostaDTO | null;

  aberto: boolean;

  aoFechar: () => void;

  aoSucesso: () => void;
}

const ModalAbrirSessao = ({ pauta, aberto, aoFechar, aoSucesso }: ModalAbrirSessaoProps) => {
  const [tempo, setTempo] = useState<string>('1');
  const [salvando, setSalvando] = useState(false);

  const calcularPrevisao = () => {
    let tempoNumerico = parseInt(tempo, 10);
    if (isNaN(tempoNumerico) || tempoNumerico <= 0) tempoNumerico = 1;

    const agora = new Date();
    agora.setMinutes(agora.getMinutes() + tempoNumerico);
    return formatarDataHora(agora.toISOString());
  };

  const limparFormulario = () => {
    setTempo('1');
  };

  const fechar = () => {
    if (salvando) return;
    limparFormulario();
    aoFechar();
  };

  const aoSubmeter = async () => {
    if (!pauta?.id) return;

    let tempoNumerico = parseInt(tempo, 10);
    if (isNaN(tempoNumerico) || tempoNumerico <= 0) {
      tempoNumerico = 1; 
    }

    setSalvando(true);

    try {
      await abrirSessao({
        pautaId: pauta.id,
        tempoEmMinutos: tempoNumerico,
      });

      toast.success('Sessão de votação aberta com sucesso!');
      limparFormulario();
      aoSucesso();
      aoFechar();
    } catch (erro) {
      console.error('Erro ao abrir sessão:', erro);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <Dialog
      open={aberto}
      onClose={fechar}
      fullWidth
      maxWidth="sm"
      PaperProps={{ sx: { borderRadius: 3 } }}
    >
      <DialogTitle sx={{ fontWeight: 700, pb: 1 }}>
        Abrir Sessão de Votação
      </DialogTitle>

      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 3, pt: 1 }}>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', bgcolor: '#f8fafc', p: 1.5, borderRadius: 2, border: '1px solid #e2e8f0' }}>
          <Box>
            <Typography variant="caption" color="text.secondary" fontWeight={600} display="block">Pauta Criada em</Typography>
            <Typography variant="caption" fontWeight={700} color="text.primary">{formatarDataHora(pauta?.dataHoraCriacao)}</Typography>
          </Box>
          <Box sx={{ ml: 'auto', textAlign: 'right' }}>
            <Typography variant="caption" color="primary" fontWeight={600} display="block">Previsão de Encerramento</Typography>
            <Typography variant="body2" fontWeight={800} color="primary.main">{calcularPrevisao()}</Typography>
          </Box>
        </Box>

        <DialogContentText sx={{ mb: 1 }}>
          Defina o tempo de duração da sessão de votação. Se deixar em branco ou zero, será assumido o tempo padrão de 1 minuto.
        </DialogContentText>

        <TextField
          label="Duração da Sessão"
          type="number"
          value={tempo}
          onChange={(e) => setTempo(e.target.value)}
          fullWidth
          autoFocus
          InputProps={{
            endAdornment: <InputAdornment position="end">minutos</InputAdornment>,
            inputProps: { min: 1, max: 1440 }
          }}
        />
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={fechar} disabled={salvando} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={aoSubmeter}
          variant="contained"
          color="primary"
          disabled={salvando}
          startIcon={salvando ? <CircularProgress size={18} color="inherit" /> : null}
        >
          {salvando ? 'Abrindo...' : 'Abrir Sessão'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ModalAbrirSessao;
