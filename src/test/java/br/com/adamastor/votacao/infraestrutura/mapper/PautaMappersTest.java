package br.com.adamastor.votacao.infraestrutura.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosCriacaoPautaDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.CriarPautaRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.PautaWebMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.PautaPersistenciaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PautaMappersTest {

    private final PautaWebMapper webMapper = Mappers.getMapper(PautaWebMapper.class);
    private final PautaPersistenciaMapper persistenciaMapper = Mappers.getMapper(PautaPersistenciaMapper.class);

    @Test
    @DisplayName("WebMapper: Deve converter Requisicao para DTO de Aplicacao")
    void deveConverterRequisicaoParaDto() {
        var req = new CriarPautaRequisicaoDTO("Titulo", "Descricao");
        DadosCriacaoPautaDTO dto = webMapper.paraDtoAplicacao(req);

        assertThat(dto.titulo()).isEqualTo(req.titulo());
        assertThat(dto.descricao()).isEqualTo(req.descricao());
    }

    @Test
    @DisplayName("PersistenciaMapper: Deve converter Dominio para Entidade JPA")
    void deveConverterDominioParaEntidade() {
        var dominio = Pauta.builder()
                .id(UUID.randomUUID())
                .titulo("Titulo")
                .dataHoraCriacao(Instant.now())
                .build();

        PautaEntidade entidade = persistenciaMapper.paraEntidade(dominio);

        assertThat(entidade.getId()).isEqualTo(dominio.getId());
        assertThat(entidade.getTitulo()).isEqualTo(dominio.getTitulo());
        assertThat(entidade.getDataHoraCriacao()).isEqualTo(dominio.getDataHoraCriacao());
    }
}
