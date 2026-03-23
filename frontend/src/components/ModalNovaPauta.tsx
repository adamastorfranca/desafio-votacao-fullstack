import { useState } from 'react';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';
import { toast } from 'react-toastify';

import { criarPauta } from '../services/pautaService';

interface ModalNovaPautaProps {

  aberto: boolean;

  aoFechar: () => void;

  aoSucesso: () => void;
}

const TITULO_MIN = 3;
const TITULO_MAX = 255;
const DESCRICAO_MAX = 2000;

const ModalNovaPauta = ({ aberto, aoFechar, aoSucesso }: ModalNovaPautaProps) => {
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [salvando, setSalvando] = useState(false);
  const [erroTitulo, setErroTitulo] = useState('');

  const limparFormulario = () => {
    setTitulo('');
    setDescricao('');
    setErroTitulo('');
  };

  const fechar = () => {
    if (salvando) return; 
    limparFormulario();
    aoFechar();
  };

  const validar = (): boolean => {
    const tituloTrimado = titulo.trim();

    if (tituloTrimado.length < TITULO_MIN) {
      setErroTitulo(`O título deve ter pelo menos ${TITULO_MIN} caracteres.`);
      return false;
    }

    if (tituloTrimado.length > TITULO_MAX) {
      setErroTitulo(`O título deve ter no máximo ${TITULO_MAX} caracteres.`);
      return false;
    }

    setErroTitulo('');
    return true;
  };

  const aoSubmeter = async () => {
    if (!validar()) return;

    setSalvando(true);

    try {
      await criarPauta({
        titulo: titulo.trim(),
        descricao: descricao.trim() || undefined,
      });

      toast.success('Pauta criada com sucesso!');
      limparFormulario();
      aoSucesso();
      aoFechar();
    } catch (erro) {
      console.error('Erro ao salvar pauta:', erro);
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
        Nova Pauta
      </DialogTitle>

      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
        <TextField
          label="Título"
          placeholder="Ex: Aprovação do orçamento anual"
          value={titulo}
          onChange={(e) => {
            setTitulo(e.target.value);
            if (erroTitulo) setErroTitulo('');
          }}
          error={!!erroTitulo}
          helperText={erroTitulo || `${titulo.length}/${TITULO_MAX} caracteres`}
          inputProps={{ maxLength: TITULO_MAX }}
          required
          fullWidth
          autoFocus
          sx={{ mt: 1 }}
        />

        <TextField
          label="Descrição (opcional)"
          placeholder="Descreva o contexto da pauta..."
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          helperText={`${descricao.length}/${DESCRICAO_MAX} caracteres`}
          inputProps={{ maxLength: DESCRICAO_MAX }}
          multiline
          rows={3}
          fullWidth
        />
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={fechar} disabled={salvando} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={aoSubmeter}
          variant="contained"
          disabled={salvando}
          startIcon={salvando ? <CircularProgress size={18} color="inherit" /> : null}
        >
          {salvando ? 'Salvando...' : 'Salvar'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ModalNovaPauta;
