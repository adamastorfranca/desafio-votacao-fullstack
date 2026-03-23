export type OpcaoVoto = 'SIM' | 'NAO';

export interface RegistrarVotoRequisicaoDTO {
  cpfAssociado: string;
  opcao: OpcaoVoto;
}

export interface VotoRespostaDTO {
  id: string;
  sessaoId: string;
  cpfAssociado: string;
  opcao: OpcaoVoto;
  dataHoraCriacao: string;
}
