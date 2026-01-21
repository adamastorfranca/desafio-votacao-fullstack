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
    private Long totalVotos = 0L;

    @Builder.Default
    private Long totalSim = 0L;

    @Builder.Default
    private Long totalNao = 0L;

    private SessaoResultado resultado;

    public boolean isAberta() {
        return SessaoStatus.ABERTA.equals(this.status) &&
                Instant.now().isBefore(this.dataHoraTermino);
    }

    public void registrarResultado(
            long totalVotos,
            long totalSim,
            long totalNao,
            String resultado
    ) {
        this.totalVotos = totalVotos;
        this.totalSim = totalSim;
        this.totalNao = totalNao;
        this.resultado = SessaoResultado.aPartirDe(resultado);
    }
}