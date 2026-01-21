package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record VotoRespostaDTO(
        UUID id,
        UUID sessaoId,
        String cpfAssociado,
        VotoOpcao opcao,
        Instant dataHoraCriacao
) {
}

