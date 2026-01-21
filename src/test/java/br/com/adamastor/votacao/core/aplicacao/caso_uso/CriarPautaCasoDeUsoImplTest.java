package br.com.adamastor.votacao.core.aplicacao.caso_uso;

import br.com.adamastor.votacao.core.aplicacao.dto.DadosCriacaoPautaDTO;
import br.com.adamastor.votacao.core.aplicacao.porta.saida.PortaRepositorioPauta;
import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarPautaCasoDeUsoImplTest {

    @Mock
    private PortaRepositorioPauta portaRepositorioPauta;

    @InjectMocks
    private CriarPautaCasoDeUsoImpl casoDeUso;

    @Test
    @DisplayName("Deve criar pauta com data de criação preenchida e chamar repositório")
    void deveCriarPautaComSucesso() {
        // Arrange
        var dados = new DadosCriacaoPautaDTO("Pauta Teste", "Descrição Teste");

        var pautaSalva = Pauta.builder()
                .id(UUID.randomUUID())
                .titulo(dados.titulo())
                .descricao(dados.descricao())
                .build();

        when(portaRepositorioPauta.salvar(any(Pauta.class))).thenReturn(pautaSalva);

        // Act
        var resultado = casoDeUso.executar(dados);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(pautaSalva.getId());

        var pautaCaptor = ArgumentCaptor.forClass(Pauta.class);
        verify(portaRepositorioPauta).salvar(pautaCaptor.capture());

        var pautaCapturada = pautaCaptor.getValue();
        assertThat(pautaCapturada.getTitulo()).isEqualTo(dados.titulo());
        assertThat(pautaCapturada.getDataHoraCriacao()).isNotNull();
    }
}