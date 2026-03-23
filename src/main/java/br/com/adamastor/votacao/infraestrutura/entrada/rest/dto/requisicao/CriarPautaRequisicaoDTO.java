package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CriarPautaRequisicaoDTO(
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 3, max = 255, message = "O título deve ter entre 3 e 255 caracteres")
        String titulo,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String descricao
) {
}
