package br.com.adamastor.votacao.infraestrutura.saida.mensageria.adaptador;

import br.com.adamastor.votacao.core.aplicacao.dto.ResultadoVotacaoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Adaptador Publicador de Resultado no Kafka")
class AdaptadorPublicadorResultadoKafkaTest {

    @InjectMocks
    private AdaptadorPublicadorResultadoKafka adaptador;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final String TOPICO_RESULTADO = "sessao-resultado-topic-teste";
    private UUID sessaoId;
    private ResultadoVotacaoDTO resultadoVotacao;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(adaptador, "topicoResultado", TOPICO_RESULTADO);

        sessaoId = UUID.randomUUID();
        resultadoVotacao = new ResultadoVotacaoDTO(
                100L,
                60L,
                40L,
                "SIM"
        );
    }

    @Test
    @DisplayName("Deve publicar resultado com sucesso quando Kafka está disponível")
    void devePublicarResultadoComSucesso() {
        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertDoesNotThrow(() -> adaptador.publicar(sessaoId, resultadoVotacao));

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoVotacao)
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falha ao enviar resultado")
    void deveLancarExcecaoQuandoFalhaKafka() {
        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(
                        new RuntimeException("Erro de conexão Kafka")
                ));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adaptador.publicar(sessaoId, resultadoVotacao)
        );

        assert exception.getMessage().contains("Falha ao enviar resultado para processamento");
        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoVotacao)
        );
    }

    @Test
    @DisplayName("Deve publicar resultado com votação unânime SIM")
    void devePublicarResultadoVotacaoUnanimeSim() {
        var resultadoUnanimeSim = new ResultadoVotacaoDTO(50L, 50L, 0L, "SIM");

        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertDoesNotThrow(() -> adaptador.publicar(sessaoId, resultadoUnanimeSim));

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoUnanimeSim)
        );
    }

    @Test
    @DisplayName("Deve publicar resultado com votação unânime NÃO")
    void devePublicarResultadoVotacaoUnAnimeNao() {
        var resultadoUnAnimeNao = new ResultadoVotacaoDTO(50L, 0L, 50L, "NÃO");

        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertDoesNotThrow(() -> adaptador.publicar(sessaoId, resultadoUnAnimeNao));

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoUnAnimeNao)
        );
    }

    @Test
    @DisplayName("Deve publicar resultado com votação empatada")
    void devePublicarResultadoVotacaoEmpatada() {
        var resultadoEmpatado = new ResultadoVotacaoDTO(100L, 50L, 50L, "EMPATE");

        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertDoesNotThrow(() -> adaptador.publicar(sessaoId, resultadoEmpatado));

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoEmpatado)
        );
    }

    @Test
    @DisplayName("Deve publicar resultado mesmo com zero votos")
    void devePublicarResultadoComZeroVotos() {
        var resultadoZeroVotos = new ResultadoVotacaoDTO(0L, 0L, 0L, "NENHUM");

        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertDoesNotThrow(() -> adaptador.publicar(sessaoId, resultadoZeroVotos));

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                eq(resultadoZeroVotos)
        );
    }

    @Test
    @DisplayName("Deve usar a chave sessaoId corretamente no envio")
    void deveUsarSessaoIdComoChave() {
        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        adaptador.publicar(sessaoId, resultadoVotacao);

        verify(kafkaTemplate).send(
                eq(TOPICO_RESULTADO),
                eq(sessaoId.toString()),
                any()
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando ExecutionException ocorre")
    void deveLancarExcecaoComExecutionException() {
        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(
                        new InterruptedException("Thread foi interrompida")
                ));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adaptador.publicar(sessaoId, resultadoVotacao)
        );

        assert exception.getMessage().contains("Falha ao enviar resultado para processamento");
    }

    @Test
    @DisplayName("Deve publicar resultado com IDs únicos de sessão")
    void devePublicarResultadosComIdsUnicos() {
        when(kafkaTemplate.send(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        UUID sessaoId1 = UUID.randomUUID();
        UUID sessaoId2 = UUID.randomUUID();

        adaptador.publicar(sessaoId1, resultadoVotacao);
        adaptador.publicar(sessaoId2, resultadoVotacao);

        verify(kafkaTemplate).send(eq(TOPICO_RESULTADO), eq(sessaoId1.toString()), eq(resultadoVotacao));
        verify(kafkaTemplate).send(eq(TOPICO_RESULTADO), eq(sessaoId2.toString()), eq(resultadoVotacao));
    }
}

