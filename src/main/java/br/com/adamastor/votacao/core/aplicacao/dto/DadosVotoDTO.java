package br.com.adamastor.votacao.core.aplicacao.dto;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;

import java.util.UUID;

public record DadosVotoDTO(
        UUID sessaoId,
        String cpfAssociado,
        VotoOpcao opcao
) {
}
