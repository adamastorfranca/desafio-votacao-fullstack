package br.com.adamastor.votacao.core.dominio.modelo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessaoResultado {

    SIM("Sim"),
    NAO("Não"),
    EMPATE("Empate");

    private final String descricao;

}