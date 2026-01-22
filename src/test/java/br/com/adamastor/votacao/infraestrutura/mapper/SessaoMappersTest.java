package br.com.adamastor.votacao.infraestrutura.mapper;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosAberturaSessaoDTO;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.requisicao.AbrirSessaoRequisicaoDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.dto.resposta.SessaoRespostaDTO;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.mapper.SessaoWebMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.SessaoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.SessaoPersistenciaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoMappersTest {

    private final SessaoWebMapper webMapper = Mappers.getMapper(SessaoWebMapper.class);
    private final SessaoPersistenciaMapper persistenciaMapper = Mappers.getMapper(SessaoPersistenciaMapper.class);

    @Test
    @DisplayName("SessaoWebMapper: Deve converter Requisicao para DTO de Aplicacao")
    void deveConverterRequisicaoParaDtoAplicacao() {
        var pautaId = UUID.randomUUID();
        var tempoEmMinutos = 5L;
        var requisicao = new AbrirSessaoRequisicaoDTO(pautaId, tempoEmMinutos);

        DadosAberturaSessaoDTO dto = webMapper.paraDtoAplicacao(requisicao);

        assertThat(dto.pautaId()).isEqualTo(pautaId);
        assertThat(dto.tempoEmMinutos()).isEqualTo(tempoEmMinutos);
    }

    @Test
    @DisplayName("SessaoWebMapper: Deve converter Requisicao com tempo nulo para DTO de Aplicacao")
    void deveConverterRequisicaoComTempoNuloParaDtoAplicacao() {
        var pautaId = UUID.randomUUID();
        var requisicao = new AbrirSessaoRequisicaoDTO(pautaId, null);

        DadosAberturaSessaoDTO dto = webMapper.paraDtoAplicacao(requisicao);

        assertThat(dto.pautaId()).isEqualTo(pautaId);
        assertThat(dto.tempoEmMinutos()).isNull();
    }

    @Test
    @DisplayName("SessaoWebMapper: Deve converter Dominio para Resposta DTO")
    void deveConverterDominioParaRespostaDto() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var dataHoraInicio = Instant.now();
        var dataHoraTermino = dataHoraInicio.plusSeconds(300);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .pautaId(pautaId)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraTermino(dataHoraTermino)
                .status(SessaoStatus.ABERTA)
                .build();

        SessaoRespostaDTO resposta = webMapper.paraResposta(sessao);

        assertThat(resposta.id()).isEqualTo(sessaoId);
        assertThat(resposta.pautaId()).isEqualTo(pautaId);
        assertThat(resposta.dataHoraInicio()).isEqualTo(dataHoraInicio);
        assertThat(resposta.dataHoraTermino()).isEqualTo(dataHoraTermino);
        assertThat(resposta.status()).isEqualTo("Aberta");
    }

    @Test
    @DisplayName("SessaoWebMapper: Deve mapear status FECHADA corretamente")
    void deveMapearStatusFechadaCorretamente() {
        var sessao = Sessao.builder()
                .id(UUID.randomUUID())
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(Instant.now())
                .dataHoraTermino(Instant.now().plusSeconds(300))
                .status(SessaoStatus.FECHADA)
                .build();

        SessaoRespostaDTO resposta = webMapper.paraResposta(sessao);

        assertThat(resposta.status()).isEqualTo("Fechada");
    }

    @Test
    @DisplayName("SessaoPersistenciaMapper: Deve converter Dominio para Entidade JPA")
    void deveConverterDominioParaEntidade() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var dataHoraInicio = Instant.now();
        var dataHoraTermino = dataHoraInicio.plusSeconds(300);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .pautaId(pautaId)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraTermino(dataHoraTermino)
                .status(SessaoStatus.ABERTA)
                .totalVotos(10L)
                .totalSim(7L)
                .totalNao(3L)
                .resultado(SessaoResultado.APROVADA)
                .build();

        SessaoEntidade entidade = persistenciaMapper.paraEntidade(sessao);

        assertThat(entidade.getId()).isEqualTo(sessaoId);
        assertThat(entidade.getPautaId()).isEqualTo(pautaId);
        assertThat(entidade.getDataHoraInicio()).isEqualTo(dataHoraInicio);
        assertThat(entidade.getDataHoraTermino()).isEqualTo(dataHoraTermino);
        assertThat(entidade.getStatus()).isEqualTo(SessaoStatus.ABERTA);
        assertThat(entidade.getTotalVotos()).isEqualTo(10L);
        assertThat(entidade.getTotalSim()).isEqualTo(7L);
        assertThat(entidade.getTotalNao()).isEqualTo(3L);
        assertThat(entidade.getResultado()).isEqualTo(SessaoResultado.APROVADA);
    }

    @Test
    @DisplayName("SessaoPersistenciaMapper: Deve mapear opcaoGanhadora para resultado")
    void deveMapearOpcaoGanhadoraParaResultado() {
        var sessao = Sessao.builder()
                .id(UUID.randomUUID())
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(Instant.now())
                .dataHoraTermino(Instant.now().plusSeconds(300))
                .status(SessaoStatus.FECHADA)
                .resultado(SessaoResultado.REPROVADA)
                .build();

        SessaoEntidade entidade = persistenciaMapper.paraEntidade(sessao);

        assertThat(entidade.getResultado()).isEqualTo(SessaoResultado.REPROVADA);
    }

    @Test
    @DisplayName("SessaoPersistenciaMapper: Deve converter Entidade JPA para Dominio")
    void deveConverterEntidadeParaDominio() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var dataHoraInicio = Instant.now();
        var dataHoraTermino = dataHoraInicio.plusSeconds(300);

        var entidade = SessaoEntidade.builder()
                .id(sessaoId)
                .pautaId(pautaId)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraTermino(dataHoraTermino)
                .status(SessaoStatus.FECHADA)
                .totalVotos(5)
                .totalSim(3)
                .totalNao(2)
                .resultado(SessaoResultado.APROVADA)
                .build();

        Sessao sessao = persistenciaMapper.paraDominio(entidade);

        assertThat(sessao.getId()).isEqualTo(sessaoId);
        assertThat(sessao.getPautaId()).isEqualTo(pautaId);
        assertThat(sessao.getDataHoraInicio()).isEqualTo(dataHoraInicio);
        assertThat(sessao.getDataHoraTermino()).isEqualTo(dataHoraTermino);
        assertThat(sessao.getStatus()).isEqualTo(SessaoStatus.FECHADA);
        assertThat(sessao.getTotalVotos()).isEqualTo(5);
        assertThat(sessao.getTotalSim()).isEqualTo(3);
        assertThat(sessao.getTotalNao()).isEqualTo(2);
        assertThat(sessao.getResultado()).isEqualTo(SessaoResultado.APROVADA);
    }

    @Test
    @DisplayName("SessaoPersistenciaMapper: Deve mapear resultado para opcaoGanhadora")
    void deveMapearResultadoParaOpcaoGanhadora() {
        var entidade = SessaoEntidade.builder()
                .id(UUID.randomUUID())
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(Instant.now())
                .dataHoraTermino(Instant.now().plusSeconds(300))
                .status(SessaoStatus.ABERTA)
                .resultado(SessaoResultado.REPROVADA)
                .build();

        Sessao sessao = persistenciaMapper.paraDominio(entidade);

        assertThat(sessao.getResultado()).isEqualTo(SessaoResultado.REPROVADA);
    }

    @Test
    @DisplayName("SessaoPersistenciaMapper: Deve converter Entidade com resultado nulo")
    void deveConverterEntidadeComResultadoNulo() {
        var entidade = SessaoEntidade.builder()
                .id(UUID.randomUUID())
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(Instant.now())
                .dataHoraTermino(Instant.now().plusSeconds(300))
                .status(SessaoStatus.ABERTA)
                .resultado(null)
                .build();

        Sessao sessao = persistenciaMapper.paraDominio(entidade);

        assertThat(sessao.getResultado()).isNull();
    }
}

