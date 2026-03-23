import { useState, useEffect } from 'react';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
  Box,
  ToggleButtonGroup,
  ToggleButton,
} from '@mui/material';
import { toast } from 'react-toastify';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import AccessTimeIcon from '@mui/icons-material/AccessTime';

import { registrarVoto } from '../services/votoService';
import type { PautaDetalhadaRespostaDTO } from '../types';
import { formatarDataHora, converterParaData } from '../utils/formatadores';

interface ModalVotarProps {
  pauta: PautaDetalhadaRespostaDTO | null;
  aberto: boolean;
  aoFechar: () => void;
  aoSucesso: () => void;
}

const formatarCPF = (valor: string) => {
  return valor
    .replace(/\D/g, '') 
    .replace(/(\d{3})(\d)/, '$1.$2') 
    .replace(/(\d{3})(\d)/, '$1.$2') 
    .replace(/(\d{3})(\d{1,2})/, '$1-$2') 
    .replace(/(-\d{2})\d+?$/, '$1'); 
};

const ModalVotar = ({ pauta, aberto, aoFechar, aoSucesso }: ModalVotarProps) => {
  const [cpf, setCpf] = useState('');
  const [opcao, setOpcao] = useState<'SIM' | 'NAO' | null>(null);
  const [salvando, setSalvando] = useState(false);
  const [erroCpf, setErroCpf] = useState('');
  const [tempoRestante, setTempoRestante] = useState<string>('--:--:--');

  useEffect(() => {
    if (!aberto || !pauta?.dataHoraTermino) return;

    const calcularTempo = () => {
      const agora = new Date().getTime();
      const termino = converterParaData(pauta.dataHoraTermino!).getTime();
      const diff = termino - agora;

      if (diff <= 0) {
        setTempoRestante('00:00:00');
        return;
      }

      const horas = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutos = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const segundos = Math.floor((diff % (1000 * 60)) / 1000);

      setTempoRestante(
        `${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`
      );
    };

    calcularTempo();
    const interval = setInterval(calcularTempo, 1000);

    return () => clearInterval(interval);
  }, [aberto, pauta]);

  const limparFormulario = () => {
    setCpf('');
    setOpcao(null);
    setErroCpf('');
  };

  const fechar = () => {
    if (salvando) return;
    limparFormulario();
    aoFechar();
  };

  const validarCpf = () => {
    const cpfLimpo = cpf.replace(/\D/g, '');

    if (!cpfLimpo || cpfLimpo.length !== 11) {
      setErroCpf('O CPF deve ter exatamente 11 dígitos.');
      return false;
    }

    if (/^(\d)\1+$/.test(cpfLimpo)) {
      setErroCpf('CPF inválido (todos os dígitos iguais).');
      return false;
    }

    let soma = 0;
    for (let i = 0; i < 9; i++) {
      soma += parseInt(cpfLimpo.charAt(i)) * (10 - i);
    }
    let resto = 11 - (soma % 11);
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpfLimpo.charAt(9))) {
      setErroCpf('O CPF inserido é matematicamente inválido.');
      return false;
    }

    soma = 0;
    for (let i = 0; i < 10; i++) {
      soma += parseInt(cpfLimpo.charAt(i)) * (11 - i);
    }
    resto = 11 - (soma % 11);
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpfLimpo.charAt(10))) {
      setErroCpf('O CPF inserido é matematicamente inválido.');
      return false;
    }

    setErroCpf('');
    return true;
  };

  const aoSubmeter = async () => {
    if (!pauta?.idSessao) {
      toast.error('Sessão de votação inválida.');
      return;
    }

    if (!validarCpf()) return;

    if (!opcao) {
      toast.warning('Por favor, selecione SIM ou NÃO antes de votar.');
      return;
    }

    setSalvando(true);

    try {
      await registrarVoto(pauta.idSessao, {
        cpfAssociado: cpf, 
        opcao,
      });

      toast.success('Voto registado com sucesso!');
      limparFormulario();
      aoSucesso();
      aoFechar();
    } catch {

      setCpf('');
      setOpcao(null);
    } finally {
      setSalvando(false);
    }
  };

  return (
    <Dialog
      open={aberto}
      onClose={fechar}
      fullWidth
      maxWidth="xs"
      PaperProps={{ sx: { borderRadius: 3 } }}
    >
      <DialogTitle sx={{ fontWeight: 700, pb: 1, textAlign: 'center' }}>
        Registar Voto
      </DialogTitle>

      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 3, pt: 2 }}>
        <Box sx={{ bgcolor: '#f8fafc', p: 2.5, borderRadius: 2, border: '1px solid #e2e8f0' }}>
          <Typography variant="caption" color="text.primary" fontWeight={800} textTransform="uppercase" letterSpacing={0.5} mb={0.5} display="block">
            Detalhes da Pauta
          </Typography>
          <Typography variant="subtitle1" fontWeight={700} color="text.primary" sx={{ lineHeight: 1.3, mb: 1 }}>
            {pauta?.titulo}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {pauta?.descricao || 'Sem descrição adicional disponibilizada para esta pauta.'}
          </Typography>

          <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 1.5 }}>
            <Box sx={{ bgcolor: '#fff', p: 1.5, borderRadius: 2, border: '1px solid #f1f5f9' }}>
              <Typography variant="caption" color="text.secondary" fontWeight={600} display="block">Criada em</Typography>
              <Typography variant="caption" fontWeight={700} color="text.primary">{formatarDataHora(pauta?.dataHoraCriacao)}</Typography>
            </Box>
            <Box sx={{ bgcolor: '#fff', p: 1.5, borderRadius: 2, border: '1px solid #f1f5f9' }}>
              <Typography variant="caption" color="text.secondary" fontWeight={600} display="block">Iniciada em</Typography>
              <Typography variant="caption" fontWeight={700} color="text.primary">{formatarDataHora(pauta?.dataHoraInicio)}</Typography>
            </Box>
            <Box sx={{ bgcolor: '#fff', p: 1.5, borderRadius: 2, border: '1px solid #f1f5f9', gridColumn: 'span 2' }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" fontWeight={600} display="block">Encerra em</Typography>
                  <Typography variant="subtitle2" fontWeight={700} color="text.primary">{formatarDataHora(pauta?.dataHoraTermino)}</Typography>
                </Box>
                <Box sx={{ textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                  <Typography variant="caption" color="error" fontWeight={700} display="block" textTransform="uppercase" letterSpacing={0.5} sx={{ mb: 0.5 }}>
                    Tempo Restante
                  </Typography>
                  <Box sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    gap: 0.75, 
                    bgcolor: '#fef2f2',
                    color: '#ef4444', 
                    px: 1.5, 
                    py: 0.75, 
                    borderRadius: 2,
                    border: '1px solid',
                    borderColor: '#fecaca'
                  }}>
                    <AccessTimeIcon sx={{ fontSize: 20 }} />
                    <Typography variant="body1" fontWeight={800} sx={{ fontVariantNumeric: 'tabular-nums', letterSpacing: 1 }}>
                      {tempoRestante}
                    </Typography>
                  </Box>
                </Box>
              </Box>
            </Box>
          </Box>
        </Box>

        {}
        <TextField
          label="CPF do Associado"
          placeholder="000.000.000-00"
          value={cpf}
          onChange={(e) => {
            setCpf(formatarCPF(e.target.value));
            if (erroCpf) setErroCpf('');
          }}
          error={!!erroCpf}
          helperText={erroCpf || 'Apenas para verificação. Não será exposto.'}
          fullWidth
          required
          autoFocus
        />

        {}
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
          <Typography variant="caption" color="text.secondary" fontWeight={600} textAlign="center">
            Escolha o seu voto
          </Typography>

          <ToggleButtonGroup
            value={opcao}
            exclusive
            onChange={(_, novoValor) => {
              if (novoValor !== null) setOpcao(novoValor);
            }}
            fullWidth
            sx={{ gap: 2 }}
          >
            <ToggleButton 
              value="SIM" 
              color="success"
              sx={{ 
                border: '1px solid !important', 
                borderRadius: '8px !important',
                borderColor: 'success.main',
                py: 2,
                display: 'flex',
                flexDirection: 'column',
                gap: 1
              }}
            >
              <ThumbUpAltIcon fontSize="medium" />
              <Typography fontWeight={700}>SIM</Typography>
            </ToggleButton>

            <ToggleButton 
              value="NAO" 
              color="error"
              sx={{ 
                border: '1px solid !important', 
                borderRadius: '8px !important',
                borderColor: 'error.main',
                py: 2,
                display: 'flex',
                flexDirection: 'column',
                gap: 1
              }}
            >
              <ThumbDownAltIcon fontSize="medium" />
              <Typography fontWeight={700}>NÃO</Typography>
            </ToggleButton>
          </ToggleButtonGroup>
        </Box>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 3, pt: 1, justifyContent: 'center', display: 'flex', flexDirection: 'column', gap: 1 }}>
        <Button
          onClick={aoSubmeter}
          variant="contained"
          disabled={salvando || !opcao || cpf.length < 14 }
          fullWidth
          size="large"
          startIcon={salvando ? <CircularProgress size={18} color="inherit" /> : null}
          sx={{ borderRadius: 2, fontWeight: 700, py: 1.2 }}
        >
          {salvando ? 'A Processar...' : 'Confirmar Voto'}
        </Button>
        <Button onClick={fechar} disabled={salvando} color="inherit" fullWidth>
          Cancelar
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ModalVotar;
