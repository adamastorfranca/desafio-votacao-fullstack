package br.com.adamastor.votacao.core.dominio.modelo;

import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessaoResultado {

    APROVADA("Aprovada"),
    REPROVADA("Reprovada"),
    EMPATE("Empate"),
    SEM_VOTOS("Sem votos");

    private final String descricao;

    public static SessaoResultado aPartirDe(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("A opção de resultado é obrigatória.");
        }

        try {
            return SessaoResultado.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException("Opção de resultado inválida: '" + valor + "'. Utilize 'APROVADA', 'REPROVADA', 'EMPATE' ou 'SEM_VOTOS'.");
        }
    }
}