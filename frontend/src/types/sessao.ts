export interface AbrirSessaoRequisicaoDTO {
  pautaId: string;
  tempoEmMinutos?: number;
}

export type StatusSessao = 'ABERTA' | 'ENCERRADA';

export interface SessaoRespostaDTO {
  id: string;
  pautaId: string;
  dataHoraInicio: string;
  dataHoraTermino: string;
  status: StatusSessao;
}
