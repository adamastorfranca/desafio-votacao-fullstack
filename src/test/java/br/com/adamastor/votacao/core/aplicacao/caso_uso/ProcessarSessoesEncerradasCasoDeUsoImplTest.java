package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.ContagemVotosDTO;
import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorResultado;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoResultado;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessarSessoesEncerradasCasoDeUsoImplTest {

    @Mock
    private PortaRepositorioSessao portaRepositorioSessao;

    @Mock
    private PortaRepositorioVoto portaRepositorioVoto;

    @Mock
    private PortaPublicadorResultado portaPublicadorResultado;

    @InjectMocks
    private ProcessarSessoesEncerradasCasoDeUsoImpl useCase;

    @Captor
    private ArgumentCaptor<Sessao> sessaoCaptor;

    @Captor
    private ArgumentCaptor<ResultadoVotacaoDTO> resultadoCaptor;

    @Test
    @DisplayName("Deve retornar silenciosamente quando não há sessões encerradas para processar")
    void deveRetornarQuandoNaoHaSessoesParaProcessar() {
        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(Collections.emptyList());

        useCase.executar();

        verify(portaRepositorioVoto, never()).contarVotosPorOpcao(any());
        verify(portaRepositorioSessao, never()).salvar(any());
        verify(portaPublicadorResultado, never()).publicar(any(), any());
    }

    @Test
    @DisplayName("Deve processar sessão com resultado APROVADA (mais SIMs que NÃOs)")
    void deveProcessarSessaoAprovada() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 7L),
                new ContagemVotosDTO(VotoOpcao.NAO, 3L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(10L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(7L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(3L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.APROVADA);

        verify(portaPublicadorResultado).publicar(
                eq(sessaoCapturada.getId()),
                resultadoCaptor.capture()
        );
        var resultadoPublicado = resultadoCaptor.getValue();
        assertThat(resultadoPublicado.resultado()).isEqualTo(SessaoResultado.APROVADA.name());
    }

    @Test
    @DisplayName("Deve processar sessão com resultado REPROVADA (mais NÃOs que SIMs)")
    void deveProcessarSessaoReprovada() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 2L),
                new ContagemVotosDTO(VotoOpcao.NAO, 8L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(10L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(2L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(8L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.REPROVADA);

        verify(portaPublicadorResultado).publicar(
                eq(sessaoCapturada.getId()),
                resultadoCaptor.capture()
        );
        var resultadoPublicado = resultadoCaptor.getValue();
        assertThat(resultadoPublicado.resultado()).isEqualTo(SessaoResultado.REPROVADA.name());
    }

    @Test
    @DisplayName("Deve processar sessão com resultado EMPATE (igual quantidade de SIMs e NÃOs)")
    void deveProcessarSessaoComEmpate() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 5L),
                new ContagemVotosDTO(VotoOpcao.NAO, 5L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(10L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(5L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(5L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.EMPATE);

        verify(portaPublicadorResultado).publicar(
                eq(sessaoCapturada.getId()),
                resultadoCaptor.capture()
        );
        var resultadoPublicado = resultadoCaptor.getValue();
        assertThat(resultadoPublicado.resultado()).isEqualTo(SessaoResultado.EMPATE.name());
    }

    @Test
    @DisplayName("Deve processar sessão com resultado SEM_VOTOS (nenhum voto registrado)")
    void deveProcessarSessaoSemVotos() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(Collections.emptyList());
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(0L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(0L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(0L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.SEM_VOTOS);

        verify(portaPublicadorResultado).publicar(
                eq(sessaoCapturada.getId()),
                resultadoCaptor.capture()
        );
        var resultadoPublicado = resultadoCaptor.getValue();
        assertThat(resultadoPublicado.resultado()).isEqualTo(SessaoResultado.SEM_VOTOS.name());
    }

    @Test
    @DisplayName("Deve processar sessão com apenas votos SIM")
    void deveProcessarSessaoApenasComVotosSim() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 100L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(100L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(100L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(0L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.APROVADA);
    }

    @Test
    @DisplayName("Deve processar sessão com apenas votos NÃO")
    void deveProcessarSessaoApenasComVotosNao() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.NAO, 50L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(50L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(0L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(50L);
        assertThat(sessaoCapturada.getResultado()).isEqualTo(SessaoResultado.REPROVADA);
    }

    @Test
    @DisplayName("Deve processar múltiplas sessões encerradas")
    void deveProcessarMultiplasSessoesEncerradas() {
        var sessao1Id = UUID.randomUUID();
        var sessao2Id = UUID.randomUUID();
        var pautaId1 = UUID.randomUUID();
        var pautaId2 = UUID.randomUUID();

        var sessao1 = construirSessao(sessao1Id, pautaId1);
        var sessao2 = construirSessao(sessao2Id, pautaId2);

        var votos1 = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 7L),
                new ContagemVotosDTO(VotoOpcao.NAO, 3L)
        );

        var votos2 = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 2L),
                new ContagemVotosDTO(VotoOpcao.NAO, 8L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao1, sessao2));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessao1Id)).thenReturn(votos1);
        when(portaRepositorioVoto.contarVotosPorOpcao(sessao2Id)).thenReturn(votos2);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao, times(2)).salvar(any(Sessao.class));
        verify(portaPublicadorResultado, times(2)).publicar(any(UUID.class), any(ResultadoVotacaoDTO.class));
    }

    @Test
    @DisplayName("Deve continuar processando outras sessões quando uma falhar")
    void deveContinuarProcessandoQuandoUmaSessaoFalhar() {
        var sessao1Id = UUID.randomUUID();
        var sessao2Id = UUID.randomUUID();
        var pautaId1 = UUID.randomUUID();
        var pautaId2 = UUID.randomUUID();

        var sessao1 = construirSessao(sessao1Id, pautaId1);
        var sessao2 = construirSessao(sessao2Id, pautaId2);

        var votos2 = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 5L),
                new ContagemVotosDTO(VotoOpcao.NAO, 5L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao1, sessao2));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessao1Id))
                .thenThrow(new RuntimeException("Erro ao contar votos"));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessao2Id)).thenReturn(votos2);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioVoto, times(2)).contarVotosPorOpcao(any(UUID.class));
        verify(portaPublicadorResultado, times(1)).publicar(any(UUID.class), any(ResultadoVotacaoDTO.class));
    }

    @Test
    @DisplayName("Deve registrar resultado com valores corretos na sessão")
    void deveRegistrarResultadoComValoresCorretos() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 75L),
                new ContagemVotosDTO(VotoOpcao.NAO, 25L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(100L);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(75L);
        assertThat(sessaoCapturada.getTotalNao()).isEqualTo(25L);
        assertThat(sessaoCapturada.getResultado()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, 1000L})
    @DisplayName("Deve processar sessões com diferentes quantidades de votos SIM")
    void deveProcessarComDiferentesQuantidadesVotosSim(long totalVotosSim) {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, totalVotosSim)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(sessaoCapturada.getTotalVotos()).isEqualTo(totalVotosSim);
        assertThat(sessaoCapturada.getTotalSim()).isEqualTo(totalVotosSim);
    }

    @Test
    @DisplayName("Deve publicar resultado com dados corretos")
    void devePublicarResultadoComDadosCorretos() {
        var sessaoId = UUID.randomUUID();
        var pautaId = UUID.randomUUID();
        var sessao = construirSessao(sessaoId, pautaId);

        var votos = List.of(
                new ContagemVotosDTO(VotoOpcao.SIM, 60L),
                new ContagemVotosDTO(VotoOpcao.NAO, 40L)
        );

        when(portaRepositorioSessao.buscarSessoesEncerradasNaoContabilizadas())
                .thenReturn(List.of(sessao));
        when(portaRepositorioVoto.contarVotosPorOpcao(sessaoId)).thenReturn(votos);
        when(portaRepositorioSessao.salvar(any(Sessao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.executar();

        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        verify(portaPublicadorResultado).publicar(
                eq(sessaoCapturada.getId()),
                resultadoCaptor.capture()
        );

        var resultado = resultadoCaptor.getValue();
        assertThat(resultado.totalVotos()).isEqualTo(100L);
        assertThat(resultado.totalSim()).isEqualTo(60L);
        assertThat(resultado.totalNao()).isEqualTo(40L);
        assertThat(resultado.resultado()).isNotEmpty();
    }

    private Sessao construirSessao(UUID sessaoId, UUID pautaId) {
        return Sessao.builder()
                .id(sessaoId)
                .pautaId(pautaId)
                .dataHoraInicio(Instant.now().minusSeconds(120))
                .dataHoraTermino(Instant.now().minusSeconds(60))
                .build();
    }
}

