package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@DisplayName("Testes de Integração - Controller de Pauta")
class PautaControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositorioPautaJpa repositorioPautaJpa;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso, retornar 201 Created e persistir no banco")
    void deveCriarPautaComSucesso() throws Exception {
        // Arrange
        var titulo = "Aumento de PLR 2026";
        var descricao = "Votação para aprovar o aumento da participação nos lucros.";
        var pautaRequisicao = Map.of(
                "titulo", titulo,
                "descricao", descricao
        );

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.ROTA_PAUTAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pautaRequisicao)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.descricao").value(descricao))
                .andExpect(jsonPath("$.dataHoraCriacao").isNotEmpty());

        var pautasSalvas = repositorioPautaJpa.findAll();
        assertEquals(1, pautasSalvas.size(), "Deve haver exatamente uma pauta no banco");
        assertEquals(titulo, pautasSalvas.getFirst().getTitulo());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o título for enviado vazio")
    void deveRetornarErroQuandoTituloInvalido() throws Exception {
        // Arrange
        var pautaRequisicao = Map.of(
                "titulo", "",
                "descricao", "Descrição válida"
        );

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.ROTA_PAUTAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pautaRequisicao)))
                .andExpect(status().isBadRequest());
        assertEquals(0, repositorioPautaJpa.count(), "O banco deve permanecer vazio após erro de validação");
    }
}