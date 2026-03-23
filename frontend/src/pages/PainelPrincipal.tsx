import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  CircularProgress,
  Container,
  Divider,
  Grid,
  Typography,
} from '@mui/material';
import { toast } from 'react-toastify';

import BarraFiltros from '../components/BarraFiltros';
import CardsResumo from '../components/CardsResumo';
import GraficoResumo from '../components/GraficoResumo';
import GraficoResultados from '../components/GraficoResultados';
import { obterEstatisticas } from '../services/pautaService';
import type {
  EstatisticasDashboardDTO,
  FiltroPautaDTO,
} from '../types';

const PainelPrincipal = () => {

  const [estatisticas, setEstatisticas] = useState<EstatisticasDashboardDTO | null>(null);
  const [filtros, setFiltros] = useState<FiltroPautaDTO>({});
  const [carregando, setCarregando] = useState(true);

  const carregarDados = useCallback(async () => {
    setCarregando(true);
    try {
      const stats = await obterEstatisticas(filtros);
      setEstatisticas(stats);
    } catch {
      toast.error('Erro ao carregar os dados do dashboard.');
    } finally {
      setCarregando(false);
    }
  }, [filtros]);

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  const aoFiltrar = (novosFiltros: FiltroPautaDTO) => {
    setFiltros(novosFiltros);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4">Dashboard</Typography>
        <Typography variant="body2" color="text.secondary">
          Painel de controle da assembléia cooperativa
        </Typography>
      </Box>

      {}
      <BarraFiltros filtros={filtros} aoAlterar={aoFiltrar} />

      {carregando ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress size={48} />
        </Box>
      ) : (
        <>
          {}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
              Visão Geral
            </Typography>
            <CardsResumo estatisticas={estatisticas} secao="status" />
          </Box>

          <Divider sx={{ mb: 4 }} />

          {}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
              Resultado das Votações
            </Typography>
            <CardsResumo estatisticas={estatisticas} secao="resultado" />
          </Box>

          <Divider sx={{ mb: 4 }} />

          {}
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6" fontWeight={600} sx={{ mb: 2 }}>
              Análise Visual
            </Typography>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 6 }}>
                <GraficoResumo estatisticas={estatisticas} />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <GraficoResultados estatisticas={estatisticas} />
              </Grid>
            </Grid>
          </Box>
        </>
      )}
    </Container>
  );
};

export default PainelPrincipal;
