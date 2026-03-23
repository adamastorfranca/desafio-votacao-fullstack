import axios from 'axios';
import { toast } from 'react-toastify';

const apiCliente = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

const extrairMensagemDoBackend = (dados: unknown): string | null => {
  if (dados && typeof dados === 'object') {
    const obj = dados as Record<string, unknown>;

    if (typeof obj.message === 'string' && obj.message.trim()) {
      return obj.message;
    }
    if (typeof obj.erro === 'string' && obj.erro.trim()) {
      return obj.erro;
    }
  }
  return null;
};

const MENSAGEM_FALLBACK_VOTO = 'CPF inválido ou não apto a votar nesta sessão.';

const MENSAGEM_ERRO_GENERICO = 'Ocorreu um erro ao processar a requisição.';

const MENSAGEM_SERVIDOR_INDISPONIVEL =
  'Servidor indisponível. Tente novamente mais tarde.';

apiCliente.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {

    if (!axios.isAxiosError(erro) || !erro.response) {
      const ehErroDeRede =
        axios.isAxiosError(erro) && erro.code === 'ERR_NETWORK';

      if (ehErroDeRede || !axios.isAxiosError(erro)) {
        toast.error(MENSAGEM_SERVIDOR_INDISPONIVEL);
      }

      return Promise.reject(erro);
    }

    const { status, data } = erro.response;
    const mensagemBackend = extrairMensagemDoBackend(data);

    if (mensagemBackend) {

      toast.error(mensagemBackend);
    } else if ((status === 400 || status === 404) && erro.config?.url?.includes('/votos')) {

      toast.error(MENSAGEM_FALLBACK_VOTO);
    } else {

      toast.error(MENSAGEM_ERRO_GENERICO);
    }

    return Promise.reject(erro);
  }
);

export default apiCliente;
