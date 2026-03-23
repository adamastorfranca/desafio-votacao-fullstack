import {
  Box,
  LinearProgress,
  Pagination,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from '@mui/material';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import AssessmentIcon from '@mui/icons-material/Assessment';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import ThumbUpAltIcon from '@mui/icons-material/ThumbUpAlt';
import ThumbDownAltIcon from '@mui/icons-material/ThumbDownAlt';
import Button from '@mui/material/Button';
import type { PautaDetalhadaRespostaDTO } from '../types';
import BadgeCorporativo from './BadgeCorporativo';
import { formatarDataSeparada } from '../utils/formatadores';

interface TabelaPautasProps {
  pautas: PautaDetalhadaRespostaDTO[];
  totalPaginas: number;
  paginaAtual: number;
  aoMudarPagina: (pagina: number) => void;
  aoAbrirSessao: (pautaId: string) => void;
  aoVerResultado: (pauta: PautaDetalhadaRespostaDTO) => void;
  aoVotar: (pautaId: string) => void;
}



const chipStatus = (status: string | null) => {
  switch (status) {
    case 'ABERTA':
      return <BadgeCorporativo tipo="sucesso" label="Aberta" />;
    case 'ENCERRADA':
      return <BadgeCorporativo tipo="info" label="Encerrada" />;
    default:
      return <BadgeCorporativo tipo="neutro" label="Aguardando" />;
  }
};

const resultadoPauta = (pauta: PautaDetalhadaRespostaDTO) => {
  if (pauta.statusSessao !== 'ENCERRADA') return null;
  const total = pauta.totalVotos ?? 0;
  if (total === 0) {
    return <BadgeCorporativo tipo="neutro" variant="outlined" label="Sem votos" />;
  }
  const sim = pauta.totalSim ?? 0;
  const nao = pauta.totalNao ?? 0;
  if (sim > nao) {
    return <BadgeCorporativo tipo="sucesso" label="Aprovada" />;
  }
  if (nao > sim) {
    return <BadgeCorporativo tipo="erro" label="Reprovada" />;
  }
  return <BadgeCorporativo tipo="alerta" label="Empate" />;
};

const calcularAprovacao = (pauta: PautaDetalhadaRespostaDTO): number => {
  const total = pauta.totalVotos ?? 0;
  if (total === 0) return 0;
  return Math.round(((pauta.totalSim ?? 0) / total) * 100);
};

const TabelaPautas = ({
  pautas,
  totalPaginas,
  paginaAtual,
  aoMudarPagina,
  aoAbrirSessao,
  aoVerResultado,
  aoVotar,
}: TabelaPautasProps) => {
  return (
    <Box>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow sx={{ bgcolor: '#f8f9fa' }}>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', py: 2 }}>Título da Pauta</TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', width: 160, py: 2 }}>Data de Criação</TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', width: 120, py: 2 }} align="center">Status</TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', width: 130, py: 2 }} align="center">Resultado</TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', width: 220, py: 2, display: { xs: 'none', md: 'table-cell' } }}>Apuração</TableCell>
              <TableCell sx={{ fontWeight: 600, color: 'text.secondary', width: 180, py: 2 }} align="center">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {pautas.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 6 }}>
                  <Typography variant="body2" color="text.secondary">
                    Nenhuma pauta encontrada com os filtros selecionados.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              pautas.map((pauta) => {
                const aprovacao = calcularAprovacao(pauta);
                const totalVotos = pauta.totalVotos ?? 0;
                const percentualSim = totalVotos > 0 ? Math.round(((pauta.totalSim ?? 0) / totalVotos) * 100) : 0;
                const percentualNao = totalVotos > 0 ? Math.round(((pauta.totalNao ?? 0) / totalVotos) * 100) : 0;
                const ehAguardando = !pauta.statusSessao || pauta.statusSessao === 'AGUARDANDO';
                const dataFormatada = formatarDataSeparada(pauta.dataHoraCriacao);

                return (
                  <TableRow key={pauta.id} hover>
                    <TableCell>
                      <Typography variant="body2" fontWeight={500} noWrap sx={{ maxWidth: 300 }}>
                        {pauta.titulo}
                      </Typography>
                      {pauta.descricao && (
                        <Typography variant="caption" color="text.secondary" noWrap sx={{ display: 'block', maxWidth: 300 }}>
                          {pauta.descricao}
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontWeight={500}>
                        {dataFormatada.data}
                      </Typography>
                      {dataFormatada.hora && (
                        <Typography variant="caption" color="text.secondary">
                          {dataFormatada.hora}
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="center">
                      {chipStatus(pauta.statusSessao)}
                    </TableCell>
                    <TableCell align="center">
                      {resultadoPauta(pauta) || <Typography variant="caption" color="text.disabled">—</Typography>}
                    </TableCell>
                    <TableCell sx={{ display: { xs: 'none', md: 'table-cell' } }}>
                      {pauta.statusSessao === 'ABERTA' ? (
                        <Typography variant="body2" color="primary.main" fontWeight={600}>
                          Votação Aberta
                        </Typography>
                      ) : ehAguardando ? (
                        <Typography variant="body2" color="text.secondary" fontWeight={500}>
                          Sessão Não Iniciada
                        </Typography>
                      ) : totalVotos > 0 ? (
                        <Tooltip
                          title={`Aprovação: ${percentualSim}% | Rejeição: ${percentualNao}%`}
                          arrow
                          placement="top"
                        >
                          <Box sx={{ cursor: 'pointer' }}>
                            <Box display="flex" justifyContent="space-between" sx={{ mb: 0.5 }}>
                              <Box display="flex" alignItems="center" gap={0.5}>
                                <ThumbUpAltIcon sx={{ fontSize: 14, color: '#2e7d32' }} />
                                <Typography variant="caption" fontWeight={600} color="#2e7d32">
                                  {pauta.totalSim} Sim
                                </Typography>
                              </Box>
                              <Box display="flex" alignItems="center" gap={0.5}>
                                <Typography variant="caption" fontWeight={600} color="#c62828">
                                  {pauta.totalNao} Não
                                </Typography>
                                <ThumbDownAltIcon sx={{ fontSize: 14, color: '#c62828' }} />
                              </Box>
                            </Box>
                            <LinearProgress
                              variant="determinate"
                              value={aprovacao}
                              sx={{
                                height: 8,
                                borderRadius: 4,
                                bgcolor: '#ffcdd2',
                                '& .MuiLinearProgress-bar': {
                                  bgcolor: '#2e7d32',
                                  borderRadius: 4,
                                },
                              }}
                            />
                          </Box>
                        </Tooltip>
                      ) : (
                        <Typography variant="body2" color="text.disabled" fontWeight={500}>
                          Nenhum voto registrado
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="center">
                      <Box display="flex" justifyContent="center" gap={1}>
                        {ehAguardando && (
                          <Tooltip title="Abrir Sessão de Votação">
                            <Button
                              variant="contained"
                              color="primary"
                              size="small"
                              startIcon={<PlayArrowIcon fontSize="small" />}
                              onClick={() => aoAbrirSessao(pauta.id)}
                              sx={{ minWidth: 105, py: 0.5, px: 1, textTransform: 'none', borderRadius: 1.5, fontWeight: 600, fontSize: '0.75rem', boxShadow: 'none' }}
                            >
                              Abrir Sessão
                            </Button>
                          </Tooltip>
                        )}
                        {pauta.statusSessao === 'ABERTA' && (
                          <Tooltip title="Votar nesta pauta">
                            <Button
                              variant="contained"
                              color="success"
                              size="small"
                              startIcon={<HowToVoteIcon fontSize="small" />}
                              onClick={() => aoVotar(pauta.id)}
                              sx={{ minWidth: 105, py: 0.5, px: 1, textTransform: 'none', borderRadius: 1.5, fontWeight: 600, fontSize: '0.75rem', boxShadow: 'none' }}
                            >
                              Votar
                            </Button>
                          </Tooltip>
                        )}
                        {pauta.statusSessao === 'ENCERRADA' && (
                          <Tooltip title="Ver Resultados">
                            <Button
                              variant="outlined"
                              color="primary"
                              size="small"
                              startIcon={<AssessmentIcon fontSize="small" />}
                              onClick={() => aoVerResultado(pauta)}
                              sx={{ minWidth: 105, py: 0.5, px: 1, textTransform: 'none', borderRadius: 1.5, fontWeight: 600, fontSize: '0.75rem' }}
                            >
                              Resultados
                            </Button>
                          </Tooltip>
                        )}
                      </Box>
                    </TableCell>
                  </TableRow>
                );
              })
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {totalPaginas > 1 && (
        <Box display="flex" justifyContent="center" sx={{ mt: 2, mb: 1 }}>
          <Pagination
            count={totalPaginas}
            page={paginaAtual + 1}
            onChange={(_evento, pagina) => aoMudarPagina(pagina - 1)}
            color="primary"
            shape="rounded"
          />
        </Box>
      )}
    </Box>
  );
};

export default TabelaPautas;
