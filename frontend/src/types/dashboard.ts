export interface PautaDetalhadaRespostaDTO {
  id: string;
  titulo: string;
  descricao: string | null;
  dataHoraCriacao: string;
  idSessao: string | null;
  statusSessao: string | null;
  dataHoraInicio: string | null;
  dataHoraTermino: string | null;
  totalVotos: number | null;
  totalSim: number | null;
  totalNao: number | null;
  opcaoGanhadora: string | null;
}

export interface EstatisticasDashboardDTO {
  totalPautas: number;
  pautasAguardando: number;
  pautasAbertas: number;
  pautasEncerradas: number;
  pautasAprovadas: number;
  pautasReprovadas: number;
  pautasEmpatadas: number;
  pautasSemVotos: number;
}