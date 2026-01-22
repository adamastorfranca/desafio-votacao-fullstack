package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosVotoDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaIntegradorCpf;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaPublicadorVoto;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioSessao;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioVoto;
import br.com.adamastor.votacao.core.dominio.excecao.EntidadeNaoEncontradaException;
import br.com.adamastor.votacao.core.dominio.excecao.RegraNegocioException;
import br.com.adamastor.votacao.core.dominio.modelo.Sessao;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarVotoCasoDeUsoImplTest {

    @Mock
    private PortaRepositorioVoto portaRepositorioVoto;

    @Mock
    private PortaRepositorioSessao portaRepositorioSessao;

    @Mock
    private PortaIntegradorCpf portaIntegradorCpf;

    @Mock
    private PortaPublicadorVoto portaPublicadorVoto;

    @InjectMocks
    private RegistrarVotoCasoDeUsoImpl casoDeUso;

    @Test
    @DisplayName("Deve registrar voto com sucesso quando todas as validações passam")
    void deveRegistrarVotoComSucesso() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var dados = new DadosVotoDTO(sessaoId, cpf, VotoOpcao.SIM);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .status(SessaoStatus.ABERTA)
                .dataHoraInicio(Instant.now())
                .dataHoraTermino(Instant.now().plusSeconds(3600))
                .build();

        when(portaRepositorioSessao.obterPorId(sessaoId)).thenReturn(Optional.of(sessao));
        when(portaRepositorioVoto.existeVotoDoAssociadoNaSessao(sessaoId, cpf)).thenReturn(false);
        when(portaIntegradorCpf.podeVotar(cpf)).thenReturn(true);

        var resultado = casoDeUso.executar(dados);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getCpfAssociado()).isEqualTo(cpf);
        assertThat(resultado.getOpcao()).isEqualTo(VotoOpcao.SIM);
        assertThat(resultado.getSessaoId()).isEqualTo(sessaoId);

        var votoCaptor = ArgumentCaptor.forClass(Voto.class);
        verify(portaPublicadorVoto).publicar(votoCaptor.capture());

        var votoCapturado = votoCaptor.getValue();
        assertThat(votoCapturado.getSessaoId()).isEqualTo(sessaoId);
        assertThat(votoCapturado.getCpfAssociado()).isEqualTo(cpf);
        assertThat(votoCapturado.getOpcao()).isEqualTo(VotoOpcao.SIM);
        assertThat(votoCapturado.getDataHoraCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão não é encontrada")
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var dados = new DadosVotoDTO(sessaoId, cpf, VotoOpcao.NAO);

        when(portaRepositorioSessao.obterPorId(sessaoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> casoDeUso.executar(dados))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessageContaining("Sessão não encontrada");

        verify(portaPublicadorVoto, never()).publicar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão não está aberta")
    void deveLancarExcecaoQuandoSessaoFechada() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var dados = new DadosVotoDTO(sessaoId, cpf, VotoOpcao.SIM);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .status(SessaoStatus.FECHADA)
                .dataHoraTermino(Instant.now().minusSeconds(3600))
                .build();

        when(portaRepositorioSessao.obterPorId(sessaoId)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> casoDeUso.executar(dados))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não está aberta");

        verify(portaPublicadorVoto, never()).publicar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando associado já votou")
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var dados = new DadosVotoDTO(sessaoId, cpf, VotoOpcao.SIM);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .status(SessaoStatus.ABERTA)
                .dataHoraTermino(Instant.now().plusSeconds(3600))
                .build();

        when(portaRepositorioSessao.obterPorId(sessaoId)).thenReturn(Optional.of(sessao));
        when(portaRepositorioVoto.existeVotoDoAssociadoNaSessao(sessaoId, cpf)).thenReturn(true);

        assertThatThrownBy(() -> casoDeUso.executar(dados))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já votou");

        verify(portaPublicadorVoto, never()).publicar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF não está autorizado a votar")
    void deveLancarExcecaoQuandoCpfNaoAutorizado() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var dados = new DadosVotoDTO(sessaoId, cpf, VotoOpcao.NAO);

        var sessao = Sessao.builder()
                .id(sessaoId)
                .status(SessaoStatus.ABERTA)
                .dataHoraTermino(Instant.now().plusSeconds(3600))
                .build();

        when(portaRepositorioSessao.obterPorId(sessaoId)).thenReturn(Optional.of(sessao));
        when(portaRepositorioVoto.existeVotoDoAssociadoNaSessao(sessaoId, cpf)).thenReturn(false);
        when(portaIntegradorCpf.podeVotar(cpf)).thenReturn(false);

        assertThatThrownBy(() -> casoDeUso.executar(dados))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não está autorizado");

        verify(portaPublicadorVoto, never()).publicar(any());
    }

}

