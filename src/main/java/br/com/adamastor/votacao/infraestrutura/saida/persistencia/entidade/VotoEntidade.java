package br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade;

import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
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
@Table(name = "tb_voto")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotoEntidade {

    @Id
    @Column(name = "id_voto")
    private UUID id;

    @Column(name = "id_sessao", nullable = false)
    private UUID sessaoId;

    @Column(name = "tx_cpf_associado", nullable = false)
    private String cpfAssociado;

    @Column(name = "tx_opcao_voto", nullable = false)
    @Enumerated(EnumType.STRING)
    private VotoOpcao opcao;

    @Column(name = "dh_criacao", nullable = false)
    private Instant dataHoraCriacao;

}

