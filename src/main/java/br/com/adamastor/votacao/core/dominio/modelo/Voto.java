package br.com.adamastor.votacao.core.dominio.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Voto {

    @EqualsAndHashCode.Include
    private final UUID id;

    private final UUID sessaoId;

    private final String cpfAssociado;

    private final VotoOpcao opcao;

    private final Instant dataHoraCriacao;

}