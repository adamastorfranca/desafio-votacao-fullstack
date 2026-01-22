package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.SessaoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.SessaoPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioSessaoJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdaptadorSessaoPostgresTest {

    @Mock
    private RepositorioSessaoJpa repositorioSessaoJpa;

    @Mock
    private SessaoPersistenciaMapper mapper;

    @InjectMocks
    private AdaptadorSessaoPostgres adaptador;

    @Test
    @DisplayName("Deve gerar ID se a sessão não possuir e persistir")
    void deveGerarIdSeNuloAoSalvar() {
        // Arrange
        var agora = Instant.now();
        var futuro = agora.plusSeconds(3600);

        var sessaoDominio = Sessao.builder()
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(agora)
                .dataHoraTermino(futuro)
                .status(SessaoStatus.ABERTA)
                .totalVotos(0L)
                .totalSim(0L)
                .totalNao(0L)
                .build();

        var entidadeSemId = new SessaoEntidade();
        entidadeSemId.setPautaId(sessaoDominio.getPautaId());
        entidadeSemId.setDataHoraInicio(agora);
        entidadeSemId.setDataHoraTermino(futuro);
        entidadeSemId.setStatus(SessaoStatus.ABERTA);

        var entidadeSalva = new SessaoEntidade();
        entidadeSalva.setId(UUID.randomUUID());
        entidadeSalva.setPautaId(sessaoDominio.getPautaId());
        entidadeSalva.setDataHoraInicio(agora);
        entidadeSalva.setDataHoraTermino(futuro);
        entidadeSalva.setStatus(SessaoStatus.ABERTA);
        entidadeSalva.setTotalVotos(0);
        entidadeSalva.setTotalSim(0);
        entidadeSalva.setTotalNao(0);

        when(mapper.paraEntidade(sessaoDominio)).thenReturn(entidadeSemId);
        when(repositorioSessaoJpa.save(any(SessaoEntidade.class))).thenReturn(entidadeSalva);
        when(mapper.paraDominio(entidadeSalva)).thenReturn(sessaoDominio);

        // Act
        var resultado = adaptador.salvar(sessaoDominio);

        // Assert
        var captor = ArgumentCaptor.forClass(SessaoEntidade.class);
        verify(repositorioSessaoJpa).save(captor.capture());

        var entidadeCapturada = captor.getValue();
        assertThat(entidadeCapturada.getId()).isNotNull();
        assertThat(resultado).isEqualTo(sessaoDominio);
    }

    @Test
    @DisplayName("Deve persistir sessão que já possui ID")
    void devePersistirSessaoComId() {
        // Arrange
        var idSessao = UUID.randomUUID();
        var agora = Instant.now();
        var futuro = agora.plusSeconds(3600);

        var sessaoDominio = Sessao.builder()
                .id(idSessao)
                .pautaId(UUID.randomUUID())
                .dataHoraInicio(agora)
                .dataHoraTermino(futuro)
                .status(SessaoStatus.ABERTA)
                .totalVotos(5L)
                .totalSim(3L)
                .totalNao(2L)
                .build();

        var entidadeComId = new SessaoEntidade();
        entidadeComId.setId(idSessao);
        entidadeComId.setPautaId(sessaoDominio.getPautaId());
        entidadeComId.setDataHoraInicio(agora);
        entidadeComId.setDataHoraTermino(futuro);
        entidadeComId.setStatus(SessaoStatus.ABERTA);
        entidadeComId.setTotalVotos(5);
        entidadeComId.setTotalSim(3);
        entidadeComId.setTotalNao(2);

        when(mapper.paraEntidade(sessaoDominio)).thenReturn(entidadeComId);
        when(repositorioSessaoJpa.save(any(SessaoEntidade.class))).thenReturn(entidadeComId);
        when(mapper.paraDominio(entidadeComId)).thenReturn(sessaoDominio);

        // Act
        var resultado = adaptador.salvar(sessaoDominio);

        // Assert
        var captor = ArgumentCaptor.forClass(SessaoEntidade.class);
        verify(repositorioSessaoJpa).save(captor.capture());

        var entidadeCapturada = captor.getValue();
        assertThat(entidadeCapturada.getId()).isEqualTo(idSessao);
        assertThat(resultado).isEqualTo(sessaoDominio);
    }

    @Test
    @DisplayName("Deve retornar true quando existir sessão aberta para a pauta")
    void deveRetornarTrueQuandoExisteSessaoAbertaParaPauta() {
        // Arrange
        var pautaId = UUID.randomUUID();
        when(repositorioSessaoJpa.existsByPautaIdAndStatus(pautaId, SessaoStatus.ABERTA))
                .thenReturn(true);

        // Act
        var existe = adaptador.existeSessaoAbertaParaPauta(pautaId);

        // Assert
        assertThat(existe).isTrue();
        verify(repositorioSessaoJpa).existsByPautaIdAndStatus(pautaId, SessaoStatus.ABERTA);
    }

    @Test
    @DisplayName("Deve retornar false quando não existir sessão aberta para a pauta")
    void deveRetornarFalseQuandoNaoExisteSessaoAbertaParaPauta() {
        // Arrange
        var pautaId = UUID.randomUUID();
        when(repositorioSessaoJpa.existsByPautaIdAndStatus(pautaId, SessaoStatus.ABERTA))
                .thenReturn(false);

        // Act
        var existe = adaptador.existeSessaoAbertaParaPauta(pautaId);

        // Assert
        assertThat(existe).isFalse();
        verify(repositorioSessaoJpa).existsByPautaIdAndStatus(pautaId, SessaoStatus.ABERTA);
    }

    @Test
    @DisplayName("Deve mapear resultado de sessão fechada corretamente")
    void deveMapearResultadoDeSessaoFechadaCorretamente() {
        // Arrange
        var idSessao = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var agora = Instant.now();
        var passado = agora.minusSeconds(3600);

        var sessaoDominio = Sessao.builder()
                .id(idSessao)
                .pautaId(pautaId)
                .dataHoraInicio(passado)
                .dataHoraTermino(agora)
                .status(SessaoStatus.FECHADA)
                .totalVotos(10L)
                .totalSim(6L)
                .totalNao(4L)
                .build();

        var entidadeFechada = new SessaoEntidade();
        entidadeFechada.setId(idSessao);
        entidadeFechada.setPautaId(pautaId);
        entidadeFechada.setDataHoraInicio(passado);
        entidadeFechada.setDataHoraTermino(agora);
        entidadeFechada.setStatus(SessaoStatus.FECHADA);
        entidadeFechada.setTotalVotos(10);
        entidadeFechada.setTotalSim(6);
        entidadeFechada.setTotalNao(4);

        when(mapper.paraEntidade(sessaoDominio)).thenReturn(entidadeFechada);
        when(repositorioSessaoJpa.save(any(SessaoEntidade.class))).thenReturn(entidadeFechada);
        when(mapper.paraDominio(entidadeFechada)).thenReturn(sessaoDominio);

        // Act
        var resultado = adaptador.salvar(sessaoDominio);

        // Assert
        assertThat(resultado.getStatus()).isEqualTo(SessaoStatus.FECHADA);
        assertThat(resultado.getTotalVotos()).isEqualTo(10);
        assertThat(resultado.getTotalSim()).isEqualTo(6);
        assertThat(resultado.getTotalNao()).isEqualTo(4);
    }
}

