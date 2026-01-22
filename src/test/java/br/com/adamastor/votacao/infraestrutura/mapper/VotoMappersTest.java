package br.com.adamastor.votacao.infraestrutura.mapper;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.VotoPersistenciaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VotoMappersTest {

    private final VotoPersistenciaMapper mapper = Mappers.getMapper(VotoPersistenciaMapper.class);

    @Test
    @DisplayName("Deve mapear Voto para VotoEntidade corretamente")
    void deveMapearVotoParaEntidade() {
        var sessaoId = UUID.randomUUID();
        var votoId = UUID.randomUUID();
        var cpf = "12345678900";
        var agora = Instant.now();

        var voto = Voto.builder()
                .id(votoId)
                .sessaoId(sessaoId)
                .cpfAssociado(cpf)
                .opcao(VotoOpcao.NAO)
                .dataHoraCriacao(agora)
                .build();

        var entidade = mapper.paraEntidade(voto);

        assertThat(entidade).isNotNull();
        assertThat(entidade.getId()).isEqualTo(votoId);
        assertThat(entidade.getSessaoId()).isEqualTo(sessaoId);
        assertThat(entidade.getCpfAssociado()).isEqualTo(cpf);
        assertThat(entidade.getOpcao()).isEqualTo(VotoOpcao.NAO);
        assertThat(entidade.getDataHoraCriacao()).isEqualTo(agora);
    }

    @Test
    @DisplayName("Deve mapear VotoEntidade para Voto corretamente")
    void deveMapearEntidadeParaVoto() {
        var sessaoId = UUID.randomUUID();
        var votoId = UUID.randomUUID();
        var cpf = "12345678900";
        var agora = Instant.now();

        var entidade = VotoEntidade.builder()
                .id(votoId)
                .sessaoId(sessaoId)
                .cpfAssociado(cpf)
                .opcao(VotoOpcao.SIM)
                .dataHoraCriacao(agora)
                .build();

        var voto = mapper.paraDominio(entidade);

        assertThat(voto).isNotNull();
        assertThat(voto.getId()).isEqualTo(votoId);
        assertThat(voto.getSessaoId()).isEqualTo(sessaoId);
        assertThat(voto.getCpfAssociado()).isEqualTo(cpf);
        assertThat(voto.getOpcao()).isEqualTo(VotoOpcao.SIM);
        assertThat(voto.getDataHoraCriacao()).isEqualTo(agora);
    }

}

