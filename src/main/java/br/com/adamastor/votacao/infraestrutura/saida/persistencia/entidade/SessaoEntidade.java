package br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade;

import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_sessao")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessaoEntidade {

    @Id
    @Column(name = "id_sessao")
    private UUID id;

    @Column(name = "id_pauta", nullable = false, unique = true)
    private UUID pautaId;

    @Column(name = "dh_inicio", nullable = false)
    private Instant dataHoraInicio;

    @Column(name = "dh_termino", nullable = false)
    private Instant dataHoraTermino;

    @Enumerated(EnumType.STRING)
    @Column(name = "st_situacao", nullable = false)
    private SessaoStatus status;

    @Column(name = "nr_total_votos")
    private Integer totalVotos;

    @Column(name = "nr_total_sim")
    private Integer totalSim;

    @Column(name = "nr_total_nao")
    private Integer totalNao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tx_opcao_ganhadora")
    private SessaoResultado resultado;

}