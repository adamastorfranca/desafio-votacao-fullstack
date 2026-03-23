package br.com.adamastor.votacao.core.aplicacao.dto;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;

public record ContagemVotosDTO(
        VotoOpcao opcao,
        Long quantidade
) {}
