import apiCliente from './api';
import type { AbrirSessaoRequisicaoDTO, SessaoRespostaDTO } from '../types';

export const abrirSessao = async (
  dados: AbrirSessaoRequisicaoDTO
): Promise<SessaoRespostaDTO> => {
  const resposta = await apiCliente.post<SessaoRespostaDTO>('/sessoes', dados);
  return resposta.data;
};
