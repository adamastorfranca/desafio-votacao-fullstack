package br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_pauta")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaEntidade {

    @Id
    @Column(name = "id_pauta")
    private UUID id;

    @Column(name = "tx_titulo", nullable = false)
    private String titulo;

    @Column(name = "tx_descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "dh_criacao", nullable = false)
    private Instant dataHoraCriacao;

    @OneToOne(mappedBy = "pauta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SessaoEntidade sessao;

}
