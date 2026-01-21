package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.dominio.modelo.Pauta;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.PautaPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
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
class AdaptadorPautaPostgresTest {

    @Mock
    private RepositorioPautaJpa repositorioPautaJpa;

    @Mock
    private PautaPersistenciaMapper mapper;

    @InjectMocks
    private AdaptadorPautaPostgres adaptador;

    @Test
    @DisplayName("Deve gerar ID se a pauta não possuir e persistir")
    void deveGerarIdSeNuloAoSalvar() {
        // Arrange
        var pautaDominio = Pauta.builder()
                .titulo("Teste")
                .dataHoraCriacao(Instant.now())
                .build();

        var entidadeSemId = new PautaEntidade();
        entidadeSemId.setTitulo("Teste");

        var entidadeSalva = new PautaEntidade();
        entidadeSalva.setId(UUID.randomUUID());

        when(mapper.paraEntidade(pautaDominio)).thenReturn(entidadeSemId);
        when(repositorioPautaJpa.save(any(PautaEntidade.class))).thenReturn(entidadeSalva);
        when(mapper.paraDominio(entidadeSalva)).thenReturn(pautaDominio);

        // Act
        adaptador.salvar(pautaDominio);

        // Assert
        var captor = ArgumentCaptor.forClass(PautaEntidade.class);
        verify(repositorioPautaJpa).save(captor.capture());

        var entidadeCapturada = captor.getValue();
        assertThat(entidadeCapturada.getId()).isNotNull();
    }
}