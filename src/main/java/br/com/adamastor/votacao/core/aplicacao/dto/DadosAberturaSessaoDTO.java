package br.com.adamastor.votacao.core.aplicacao.dto;

import java.util.UUID;

public record DadosAberturaSessaoDTO(
        UUID pautaId,
        Long tempoEmMinutos
) {
}