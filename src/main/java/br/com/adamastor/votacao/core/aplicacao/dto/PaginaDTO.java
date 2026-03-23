package br.com.adamastor.votacao.core.aplicacao.dto;

import java.util.List;

public record PaginaDTO<T>(
    List<T> conteudo,
    int numeroPagina,
    int tamanhoPagina,
    long totalElementos,
    int totalPaginas
) {}
