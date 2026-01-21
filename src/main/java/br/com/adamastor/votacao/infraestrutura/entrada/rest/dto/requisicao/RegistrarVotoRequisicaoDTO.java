package br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegistrarVotoRequisicaoDTO(
        @NotBlank(message = "O CPF do associado é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpfAssociado,

        @NotNull(message = "A opção de voto é obrigatória")
        VotoOpcao opcao
) {
}

