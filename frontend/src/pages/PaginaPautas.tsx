import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CircularProgress,
  Container,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { toast } from 'react-toastify';

import BarraFiltros from '../components/BarraFiltros';
import TabelaPautas from '../components/TabelaPautas';
import ModalNovaPauta from '../components/ModalNovaPauta';
import ModalResultado from '../components/ModalResultado';
import ModalAbrirSessao from '../components/ModalAbrirSessao';
import ModalVotar from '../components/ModalVotar';
import { listarPautasDetalhadas } from '../services/pautaService';
import type {
  FiltroPautaDTO,
  PautaDetalhadaRespostaDTO,
} from '../types';

const TAMANHO_PAGINA = 10;

const PaginaPautas = () => {

  const [pautas, setPautas] = useState<PautaDetalhadaRespostaDTO[]>([]);
  const [filtros, setFiltros] = useState<FiltroPautaDTO>({});
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [carregando, setCarregando] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [pautaSelecionada, setPautaSelecionada] = useState<PautaDetalhadaRespostaDTO | null>(null);

  const [modalSessaoAberto, setModalSessaoAberto] = useState(false);
  const [pautaSessao, setPautaSessao] = useState<PautaDetalhadaRespostaDTO | null>(null);

  const [modalVotarAberto, setModalVotarAberto] = useState(false);
  const [pautaVotar, setPautaVotar] = useState<PautaDetalhadaRespostaDTO | null>(null);

  const carregarDados = useCallback(async () => {
    setCarregando(true);
    try {
      const pagina = await listarPautasDetalhadas(filtros, paginaAtual, TAMANHO_PAGINA);
      setPautas(pagina.content);
      setTotalPaginas(pagina.totalPages);
    } catch {
      toast.error('Erro ao carregar as pautas.');
    } finally {
      setCarregando(false);
    }
  }, [filtros, paginaAtual]);

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  const aoFiltrar = (novosFiltros: FiltroPautaDTO) => {
    setFiltros(novosFiltros);
    setPaginaAtual(0);
  };

  const aoAbrirSessao = (pautaId: string) => {
    const pautaEcontrada = pautas.find((p) => p.id === pautaId) || null;
    setPautaSessao(pautaEcontrada);
    setModalSessaoAberto(true);
  };

  const aoVerResultado = (pauta: PautaDetalhadaRespostaDTO) => {
    setPautaSelecionada(pauta);
  };

  const aoVotar = (pautaId: string) => {
    const pautaEcontrada = pautas.find((p) => p.id === pautaId) || null;
    setPautaVotar(pautaEcontrada);
    setModalVotarAberto(true);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {}
      <Box display="flex" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
        <Box>
          <Typography variant="h4">Pautas</Typography>
          <Typography variant="body2" color="text.secondary">
            Gerir pautas da assembléia cooperativa
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setModalAberto(true)}
        >
          Nova Pauta
        </Button>
      </Box>

      {}
      <BarraFiltros filtros={filtros} aoAlterar={aoFiltrar} />

      {}
      {carregando ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress size={48} />
        </Box>
      ) : (
        <Card
          sx={{
            '&:hover': { transform: 'none', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' },
          }}
        >
          <Box sx={{ p: 2.5 }}>
            <TabelaPautas
              pautas={pautas}
              totalPaginas={totalPaginas}
              paginaAtual={paginaAtual}
              aoMudarPagina={setPaginaAtual}
              aoAbrirSessao={aoAbrirSessao}
              aoVerResultado={aoVerResultado}
              aoVotar={aoVotar}
            />
          </Box>
        </Card>
      )}

      {}
      <ModalNovaPauta
        aberto={modalAberto}
        aoFechar={() => setModalAberto(false)}
        aoSucesso={carregarDados}
      />

      {}
      <ModalResultado
        aberto={pautaSelecionada !== null}
        aoFechar={() => setPautaSelecionada(null)}
        pauta={pautaSelecionada}
      />

      {}
      <ModalAbrirSessao
        pauta={pautaSessao}
        aberto={modalSessaoAberto}
        aoFechar={() => {
          setModalSessaoAberto(false);
          setPautaSessao(null);
        }}
        aoSucesso={carregarDados}
      />

      {}
      <ModalVotar
        pauta={pautaVotar}
        aberto={modalVotarAberto}
        aoFechar={() => {
          setModalVotarAberto(false);
          setPautaVotar(null);
        }}
        aoSucesso={carregarDados}
      />
    </Container>
  );
};

export default PaginaPautas;
