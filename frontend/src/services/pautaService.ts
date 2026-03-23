import apiCliente from './api';
import type {
  CriarPautaRequisicaoDTO,
  PautaRespostaDTO,
  PautaDetalhadaRespostaDTO,
  EstatisticasDashboardDTO,
  FiltroPautaDTO,
  PaginaSpringDTO,
} from '../types';

export const criarPauta = async (
  dados: CriarPautaRequisicaoDTO
): Promise<PautaRespostaDTO> => {
  const resposta = await apiCliente.post<PautaRespostaDTO>('/pautas', dados);
  return resposta.data;
};

export const listarPautas = async (): Promise<PautaRespostaDTO[]> => {
  const resposta = await apiCliente.get<PautaRespostaDTO[]>('/pautas/simples');
  return resposta.data;
};

export const listarPautasDetalhadas = async (
  filtros: FiltroPautaDTO = {},
  pagina: number = 0,
  tamanho: number = 10
): Promise<PaginaSpringDTO<PautaDetalhadaRespostaDTO>> => {
  const resposta = await apiCliente.get<PaginaSpringDTO<PautaDetalhadaRespostaDTO>>(
    '/pautas',
    {
      params: {
        statusSessao: filtros.statusSessao || undefined,
        resultado: filtros.resultado || undefined,
        periodo: filtros.periodo || undefined,
        page: pagina,
        size: tamanho,
      },
    }
  );
  return resposta.data;
};

export const obterEstatisticas = async (
  filtros: FiltroPautaDTO = {}
): Promise<EstatisticasDashboardDTO> => {
  const resposta = await apiCliente.get<EstatisticasDashboardDTO>(
    '/pautas/estatisticas',
    {
      params: {
        statusSessao: filtros.statusSessao || undefined,
        resultado: filtros.resultado || undefined,
        periodo: filtros.periodo || undefined,
      },
    }
  );
  return resposta.data;
};
