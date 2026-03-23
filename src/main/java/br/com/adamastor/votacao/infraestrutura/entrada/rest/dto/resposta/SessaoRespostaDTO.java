package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record SessaoRespostaDTO(
        UUID id,
        UUID pautaId,
        Instant dataHoraInicio,
        Instant dataHoraTermino,
        String status
) {
}
