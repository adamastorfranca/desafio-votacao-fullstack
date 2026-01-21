package br.com.adamastor.votacao.core.dominio.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Sessao {

    @EqualsAndHashCode.Include
    private UUID id;

    private UUID pautaId;

    private Instant dataHoraInicio;

    private Instant dataHoraTermino;

    private SessaoStatus status;

    @Builder.Default
    private Integer totalVotos = 0;

    @Builder.Default
    private Integer totalSim = 0;

    @Builder.Default
    private Integer totalNao = 0;

    private SessaoResultado opcaoGanhadora;

    public boolean isAberta() {
        return SessaoStatus.ABERTA.equals(this.status) &&
                Instant.now().isBefore(this.dataHoraTermino);
    }
}