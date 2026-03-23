package br.com.adamastor.votacao.core.aplicacao.dto;

public record EstatisticasDashboardDTO(
    Long totalPautas,
    Long pautasAguardando,
    Long pautasAbertas,
    Long pautasEncerradas,
    Long pautasAprovadas,
    Long pautasReprovadas,
    Long pautasEmpatadas,
    Long pautasSemVotos
) {}
