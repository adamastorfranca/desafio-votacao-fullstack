package br.com.adamastor.votacao.core.aplicacao.dto;

public record ResultadoVotacaoDTO(
        long totalVotos,
        long totalSim,
        long totalNao,
        String resultado
) {}
