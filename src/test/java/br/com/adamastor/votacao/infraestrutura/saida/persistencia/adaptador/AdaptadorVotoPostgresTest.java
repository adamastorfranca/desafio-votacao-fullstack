package br.com.adamastor.votacao.infraestrutura.saida.persistencia.adaptador;

import br.com.adamastor.votacao.core.dominio.modelo.Voto;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.VotoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.mapper.VotoPersistenciaMapper;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioVotoJpa;
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
class AdaptadorVotoPostgresTest {

    @Mock
    private RepositorioVotoJpa repositorioVotoJpa;

    @Mock
    private VotoPersistenciaMapper mapper;

    @InjectMocks
    private AdaptadorVotoPostgres adaptador;

    @Test
    @DisplayName("Deve salvar voto corretamente no banco de dados")
    void deveSalvarVotoComSucesso() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";
        var votoId = UUID.randomUUID();

        var voto = Voto.builder()
                .id(votoId)
                .sessaoId(sessaoId)
                .cpfAssociado(cpf)
                .opcao(VotoOpcao.SIM)
                .dataHoraCriacao(Instant.now())
                .build();

        var entidade = VotoEntidade.builder()
                .id(votoId)
                .sessaoId(sessaoId)
                .cpfAssociado(cpf)
                .opcao(VotoOpcao.SIM)
                .dataHoraCriacao(Instant.now())
                .build();

        when(mapper.paraEntidade(voto)).thenReturn(entidade);
        when(repositorioVotoJpa.save(any(VotoEntidade.class))).thenReturn(entidade);
        when(mapper.paraDominio(entidade)).thenReturn(voto);

        var resultado = adaptador.salvar(voto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(votoId);
        assertThat(resultado.getCpfAssociado()).isEqualTo(cpf);

        var entidadeCaptor = ArgumentCaptor.forClass(VotoEntidade.class);
        verify(repositorioVotoJpa).save(entidadeCaptor.capture());

        var entidadeCapturada = entidadeCaptor.getValue();
        assertThat(entidadeCapturada.getSessaoId()).isEqualTo(sessaoId);
        assertThat(entidadeCapturada.getCpfAssociado()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Deve verificar existência de voto do associado na sessão")
    void deveVerificarExistenciaDeVoto() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";

        when(repositorioVotoJpa.existsBySessaoIdAndCpfAssociado(sessaoId, cpf)).thenReturn(true);

        var resultado = adaptador.existeVotoDoAssociadoNaSessao(sessaoId, cpf);

        assertThat(resultado).isTrue();
        verify(repositorioVotoJpa).existsBySessaoIdAndCpfAssociado(sessaoId, cpf);
    }

    @Test
    @DisplayName("Deve retornar false quando não existe voto do associado na sessão")
    void deveRetornarFalseQuandoNaoExisteVoto() {
        var sessaoId = UUID.randomUUID();
        var cpf = "12345678900";

        when(repositorioVotoJpa.existsBySessaoIdAndCpfAssociado(sessaoId, cpf)).thenReturn(false);

        var resultado = adaptador.existeVotoDoAssociadoNaSessao(sessaoId, cpf);

        assertThat(resultado).isFalse();
        verify(repositorioVotoJpa).existsBySessaoIdAndCpfAssociado(sessaoId, cpf);
    }

}

