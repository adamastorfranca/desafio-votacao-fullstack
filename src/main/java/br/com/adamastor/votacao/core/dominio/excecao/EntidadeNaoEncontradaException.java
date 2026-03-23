package br.com.adamastor.votacao.core.dominio.excecao;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
