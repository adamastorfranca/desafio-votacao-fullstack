import apiCliente from './api';
import type {
  RegistrarVotoRequisicaoDTO,
  VotoRespostaDTO,
  ResultadoRespostaDTO,
} from '../types';

export const registrarVoto = async (
  sessaoId: string,
  dados: RegistrarVotoRequisicaoDTO
): Promise<VotoRespostaDTO> => {
  const dadosLimpos: RegistrarVotoRequisicaoDTO = {
    ...dados,
    cpfAssociado: dados.cpfAssociado.replace(/\D/g, ''),
  };

  const resposta = await apiCliente.post<VotoRespostaDTO>(
    `/sessoes/${sessaoId}/votos`,
    dadosLimpos
  );
  return resposta.data;
};

export const obterResultado = async (
  sessaoId: string
): Promise<ResultadoRespostaDTO> => {
  const resposta = await apiCliente.get<ResultadoRespostaDTO>(
    `/sessoes/${sessaoId}/resultado`
  );
  return resposta.data;
};
