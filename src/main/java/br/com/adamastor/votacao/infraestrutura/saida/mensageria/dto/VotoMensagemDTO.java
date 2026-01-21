package br.com.adamastor.votacao.infraestrutura.saida.mensageria.dto;

import java.time.Instant;
import java.util.UUID;

public record VotoMensagemDTO(
        UUID id,
        UUID sessaoId,
        String cpfAssociado,
        String opcao,
        Instant dataHoraCriacao
) {}