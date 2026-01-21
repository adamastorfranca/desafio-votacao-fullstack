package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PautaRespostaDTO(
        UUID id,
        String titulo,
        String descricao,
        Instant dataHoraCriacao
) {
}