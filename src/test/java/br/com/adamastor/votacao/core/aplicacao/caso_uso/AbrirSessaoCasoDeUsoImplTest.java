package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosAberturaSessaoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.dominio.excecao.EntidadeNaoEncontradaException;
import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbrirSessaoCasoDeUsoImplTest {

    @Mock
    private PortaRepositorioSessao portaRepositorioSessao;

    @Mock
    private PortaRepositorioPauta portaRepositorioPauta;

    @InjectMocks
    private AbrirSessaoCasoDeUsoImpl useCase;

    @Captor
    private ArgumentCaptor<Sessao> sessaoCaptor;

    @Test
    @DisplayName("Deve abrir sessão com sucesso utilizando tempo informado")
    void deveAbrirSessaoComSucessoComTempoInformado() {
        // Arrange
        var pautaId = UUID.randomUUID();
        var tempoMinutos = 10L;
        var dados = new DadosAberturaSessaoDTO(pautaId, tempoMinutos);

        when(portaRepositorioPauta.existePorId(pautaId)).thenReturn(true);
        when(portaRepositorioSessao.existeSessaoAbertaParaPauta(pautaId)).thenReturn(false);
        when(portaRepositorioSessao.salvar(any(Sessao.class))).thenAnswer(invocation -> {
            var sessao = (Sessao) invocation.getArgument(0);
            // Simula o comportamento do banco gerando ID
            return Sessao.builder()
                    .id(UUID.randomUUID())
                    .pautaId(sessao.getPautaId())
                    .dataHoraInicio(sessao.getDataHoraInicio())
                    .dataHoraTermino(sessao.getDataHoraTermino())
                    .status(sessao.getStatus())
                    .build();
        });

        // Act
        var resultado = useCase.executar(dados);

        // Assert
        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(SessaoStatus.ABERTA);

        var duracaoCalculada = Duration.between(sessaoCapturada.getDataHoraInicio(), sessaoCapturada.getDataHoraTermino());
        assertThat(duracaoCalculada.toMinutes()).isEqualTo(tempoMinutos);
    }

    @Test
    @DisplayName("Deve abrir sessão com sucesso utilizando tempo default (1 min) quando input for nulo")
    void deveAbrirSessaoComTempoDefaultQuandoInputNulo() {
        // Arrange
        var pautaId = UUID.randomUUID();
        var dados = new DadosAberturaSessaoDTO(pautaId, null);

        when(portaRepositorioPauta.existePorId(pautaId)).thenReturn(true);
        when(portaRepositorioSessao.existeSessaoAbertaParaPauta(pautaId)).thenReturn(false);
        when(portaRepositorioSessao.salvar(any(Sessao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.executar(dados);

        // Assert
        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        var duracaoCalculada = Duration.between(sessaoCapturada.getDataHoraInicio(), sessaoCapturada.getDataHoraTermino());
        assertThat(duracaoCalculada.toMinutes()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve abrir sessão com sucesso utilizando tempo default (1 min) quando input for zero ou negativo")
    void deveAbrirSessaoComTempoDefaultQuandoInputInvalido() {
        // Arrange
        var pautaId = UUID.randomUUID();
        var dados = new DadosAberturaSessaoDTO(pautaId, 0L);

        when(portaRepositorioPauta.existePorId(pautaId)).thenReturn(true);
        when(portaRepositorioSessao.existeSessaoAbertaParaPauta(pautaId)).thenReturn(false);
        when(portaRepositorioSessao.salvar(any(Sessao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.executar(dados);

        // Assert
        verify(portaRepositorioSessao).salvar(sessaoCaptor.capture());
        var sessaoCapturada = sessaoCaptor.getValue();

        var duracaoCalculada = Duration.between(sessaoCapturada.getDataHoraInicio(), sessaoCapturada.getDataHoraTermino());
        assertThat(duracaoCalculada.toMinutes()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a pauta não existir")
    void deveLancarExcecaoQuandoPautaNaoExistir() {
        // Arrange
        var pautaId = UUID.randomUUID();
        var dados = new DadosAberturaSessaoDTO(pautaId, 10L);

        when(portaRepositorioPauta.existePorId(pautaId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.executar(dados))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessageContaining("Pauta não encontrada");

        verify(portaRepositorioSessao, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existir sessão aberta para a pauta")
    void deveLancarExcecaoQuandoSessaoJaAberta() {
        // Arrange
        var pautaId = UUID.randomUUID();
        var dados = new DadosAberturaSessaoDTO(pautaId, 10L);

        when(portaRepositorioPauta.existePorId(pautaId)).thenReturn(true);
        when(portaRepositorioSessao.existeSessaoAbertaParaPauta(pautaId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.executar(dados))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Já existe uma sessão aberta para esta pauta.");

        verify(portaRepositorioSessao, never()).salvar(any());
    }
}
