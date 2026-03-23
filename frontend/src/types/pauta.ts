export interface CriarPautaRequisicaoDTO {
  titulo: string;
  descricao?: string;
}

export interface PautaRespostaDTO {
  id: string;
  titulo: string;
  descricao: string | null;
  dataHoraCriacao: string;
}
