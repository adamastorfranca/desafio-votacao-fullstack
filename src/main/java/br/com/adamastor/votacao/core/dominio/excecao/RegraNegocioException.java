package br.com.adamastor.votacao.core.dominio.excecao;

public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
