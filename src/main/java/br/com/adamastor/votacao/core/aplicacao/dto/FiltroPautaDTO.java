package br.com.adamastor.votacao.core.aplicacao.dto;

public record FiltroPautaDTO(
    String statusSessao,
    String resultado,
    PeriodoFiltroEnum periodo
) {}
