package br.com.adamastor.votacao.core.dominio.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessaoStatus {

    ABERTA("Aberta"),
    FECHADA("Fechada");

    private final String descricao;

}