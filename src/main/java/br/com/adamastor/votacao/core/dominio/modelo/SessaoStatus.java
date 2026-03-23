package br.com.adamastor.votacao.core.dominio.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessaoStatus {

    AGUARDANDO("Aguardando"),
    ABERTA("Aberta"),
    ENCERRADA("Encerrada");

    private final String descricao;

}
