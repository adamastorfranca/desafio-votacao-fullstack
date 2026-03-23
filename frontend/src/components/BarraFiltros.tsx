import FilterListIcon from '@mui/icons-material/FilterList';
import type { SelectChangeEvent } from '@mui/material';
import {
  Box,
  Button,
  Collapse,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import { useState } from 'react';
import type { FiltroPautaDTO } from '../types';

interface BarraFiltrosProps {
  filtros: FiltroPautaDTO;
  aoAlterar: (novosFiltros: FiltroPautaDTO) => void;
}

const opcoesStatus = [
  { valor: '', rotulo: 'Todas' },
  { valor: 'AGUARDANDO', rotulo: 'Aguardando' },
  { valor: 'ABERTA', rotulo: 'Aberta' },
  { valor: 'ENCERRADA', rotulo: 'Encerrada' },
];

const opcoesResultado = [
  { valor: '', rotulo: 'Todos' },
  { valor: 'APROVADA', rotulo: 'Aprovada' },
  { valor: 'REPROVADA', rotulo: 'Reprovada' },
  { valor: 'EMPATE', rotulo: 'Empate' },
  { valor: 'SEM_VOTOS', rotulo: 'Sem Votos' },
];

const opcoesPeriodo = [
  { valor: 'TODO_PERIODO', rotulo: 'Todo o período' },
  { valor: 'HOJE', rotulo: 'Hoje' },
  { valor: 'ULTIMO_7_DIAS', rotulo: 'Últimos 7 dias' },
  { valor: 'ULTIMOS_15_DIAS', rotulo: 'Últimos 15 dias' },
  { valor: 'ULTIMO_MES', rotulo: 'Último mês' },
  { valor: 'ULTIMO_3_MESES', rotulo: 'Últimos 3 meses' },
  { valor: 'ULTIMO_6_MESES', rotulo: 'Últimos 6 meses' },
  { valor: 'ULTIMO_ANO', rotulo: 'Último ano' },
];

const BarraFiltros = ({ filtros, aoAlterar }: BarraFiltrosProps) => {
  const tema = useTheme();
  const ehMobile = useMediaQuery(tema.breakpoints.down('sm'));
  const [aberto, setAberto] = useState(false);

  const aoMudar = (campo: keyof FiltroPautaDTO) => (evento: SelectChangeEvent<string | number>) => {
    const valor = evento.target.value;
    aoAlterar({
      ...filtros,
      [campo]: valor === '' || valor === 0 ? undefined : valor,
    });
  };

  const filtrosAtivos = [
    filtros.statusSessao,
    filtros.resultado,
    filtros.periodo && filtros.periodo !== 'TODO_PERIODO' ? filtros.periodo : undefined,
  ].filter(Boolean).length;

  const conteudoFiltros = (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: { xs: '1fr', sm: 'repeat(3, 1fr)' },
        gap: 1.5,
      }}
    >
      <FormControl size="small" fullWidth>
        <InputLabel>Período</InputLabel>
        <Select
          value={filtros.periodo || 'TODO_PERIODO'}
          label="Período"
          onChange={aoMudar('periodo')}
        >
          {opcoesPeriodo.map((op) => (
            <MenuItem key={op.valor} value={op.valor}>
              {op.rotulo}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size="small" fullWidth>
        <InputLabel>Situação</InputLabel>
        <Select
          value={filtros.statusSessao || ''}
          label="Situação"
          onChange={aoMudar('statusSessao')}
        >
          {opcoesStatus.map((op) => (
            <MenuItem key={op.valor} value={op.valor}>
              {op.rotulo}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size="small" fullWidth>
        <InputLabel>Resultado</InputLabel>
        <Select
          value={filtros.resultado || ''}
          label="Resultado"
          onChange={aoMudar('resultado')}
        >
          {opcoesResultado.map((op) => (
            <MenuItem key={op.valor} value={op.valor}>
              {op.rotulo}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );

  if (!ehMobile) {
    return <Box sx={{ mb: 3 }}>{conteudoFiltros}</Box>;
  }

  return (
    <Box sx={{ mb: 3 }}>
      <Button
        variant="outlined"
        size="small"
        startIcon={<FilterListIcon />}
        onClick={() => setAberto((prev) => !prev)}
        sx={{
          mb: aberto ? 1.5 : 0,
          textTransform: 'none',
          borderColor: 'divider',
          color: 'text.secondary',
          fontWeight: 500,
        }}
      >
        Filtros{filtrosAtivos > 0 ? ` (${filtrosAtivos})` : ''}
      </Button>

      <Collapse in={aberto} timeout={250}>
        {conteudoFiltros}
      </Collapse>
    </Box>
  );
};

export default BarraFiltros;
