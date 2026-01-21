package br.com.adamastor.votacao.core.dominio.modelo;

import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;

public enum VotoOpcao {
    SIM,
    NAO;

    public static VotoOpcao aPartirDe(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("A opção de voto é obrigatória.");
        }

        try {
            return VotoOpcao.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException("Opção de voto inválida: '" + valor + "'. Utilize 'SIM' ou 'NAO'.");
        }
    }
}
