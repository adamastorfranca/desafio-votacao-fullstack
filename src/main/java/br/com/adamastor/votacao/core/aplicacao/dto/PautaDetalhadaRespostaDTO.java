package br.com.adamastor.votacao.core.aplicacao.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PautaDetalhadaRespostaDTO(
    UUID id,
    String titulo,
    String descricao,
    LocalDateTime dataHoraCriacao,
    UUID idSessao,
    String statusSessao,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraTermino,
    Integer totalVotos,
    Integer totalSim,
    Integer totalNao,
    String opcaoGanhadora
) {}