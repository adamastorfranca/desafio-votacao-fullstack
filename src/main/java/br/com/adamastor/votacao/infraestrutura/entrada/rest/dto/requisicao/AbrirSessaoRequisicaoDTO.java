package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AbrirSessaoRequisicaoDTO(
        @NotNull(message = "O ID da pauta é obrigatório")
        UUID pautaId,

        Long tempoEmMinutos
) {
}