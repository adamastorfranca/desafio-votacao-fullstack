package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.config.BaseIntegrationTest;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioSessaoJpa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Testes de Integração - Controller de Sessão")
class SessaoControllerTest extends BaseIntegrationTest {

    @Autowired
    private RepositorioSessaoJpa repositorioSessaoJpa;

    @Autowired
    private RepositorioPautaJpa repositorioPautaJpa;

    @AfterEach
    void limparBancoDeDados() {
        repositorioSessaoJpa.deleteAll();
        repositorioPautaJpa.deleteAll();
    }

    @Test
    @DisplayName("Deve abrir uma sessão com sucesso para uma pauta existente (tempo padrão)")
    void deveAbrirSessaoComSucessoTempoPadrao() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para sessão padrão");
        var requisicaoJson = """
                {
                    "pautaId": "%s"
                }
                """.formatted(pauta.getId());

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.ROTA_SESSOES_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId().toString()))
                .andExpect(jsonPath("$.dataHoraInicio").isNotEmpty())
                .andExpect(jsonPath("$.dataHoraTermino").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Aberta"));

        var sessoesSalvas = repositorioSessaoJpa.findAll();
        assertEquals(1, sessoesSalvas.size(), "Deve haver exatamente uma sessão no banco");
        assertEquals(pauta.getId(), sessoesSalvas.getFirst().getPautaId());
    }

    @Test
    @DisplayName("Deve abrir uma sessão com sucesso com tempo customizado")
    void deveAbrirSessaoComSucessoTempoCustomizado() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para sessão customizada");
        var tempoMinutos = 10L;
        var requisicaoJson = """
                {
                    "pautaId": "%s",
                    "tempoEmMinutos": %d
                }
                """.formatted(pauta.getId(), tempoMinutos);

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.ROTA_SESSOES_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Aberta"));

        var sessaoSalva = repositorioSessaoJpa.findAll().getFirst();
        assertNotNull(sessaoSalva.getDataHoraTermino());
        var duracaoEsperadaSegundos = tempoMinutos * 60;
        var duracaoReal = Duration.between(sessaoSalva.getDataHoraInicio(), sessaoSalva.getDataHoraTermino());

        assertEquals(duracaoEsperadaSegundos, duracaoReal.toSeconds(), 2L, "A duração da sessão deve corresponder ao tempo solicitado");
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o ID da pauta não for informado")
    void deveRetornarErroQuandoPautaIdNulo() throws Exception {
        // Arrange
        var requisicaoJson = """
                {
                    "tempoEmMinutos": 5
                }
                """;

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.ROTA_SESSOES_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isBadRequest());

        assertEquals(0, repositorioSessaoJpa.count(), "O banco deve permanecer vazio após erro de validação");
    }

    private PautaEntidade criarPautaNoBanco(String titulo) {
        var pauta = PautaEntidade.builder()
                .id(UUID.randomUUID())
                .titulo(titulo)
                .descricao("Descrição Teste")
                .dataHoraCriacao(Instant.now())
                .build();
        return repositorioPautaJpa.saveAndFlush(pauta);
    }
}