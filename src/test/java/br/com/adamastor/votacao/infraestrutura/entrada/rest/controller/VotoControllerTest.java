package br.com.adamastor.votacao.infraestrutura.entrada.rest.controller;

import br.com.adamastor.votacao.config.BaseIntegrationTest;
import br.com.adamastor.votacao.core.dominio.modelo.SessaoStatus;
import br.com.adamastor.votacao.core.dominio.modelo.VotoOpcao;
import br.com.adamastor.votacao.infraestrutura.entrada.rest.ApiConstantes;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.PautaEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.entidade.SessaoEntidade;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioPautaJpa;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioSessaoJpa;
import br.com.adamastor.votacao.infraestrutura.saida.persistencia.repositorio.RepositorioVotoJpa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Testes de Integração - Controller de Voto")
class VotoControllerTest extends BaseIntegrationTest {

    @Autowired
    private RepositorioVotoJpa repositorioVotoJpa;

    @Autowired
    private RepositorioSessaoJpa repositorioSessaoJpa;

    @Autowired
    private RepositorioPautaJpa repositorioPautaJpa;

    @AfterEach
    void limparBancoDeDados() {
        repositorioVotoJpa.deleteAll();
        repositorioSessaoJpa.deleteAll();
        repositorioPautaJpa.deleteAll();
    }

    @Test
    @DisplayName("Deve registrar um voto com sucesso em uma sessão aberta")
    void deveRegistrarVotoComSucesso() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para voto");
        var sessao = criarSessaoAbertaNoBanco(pauta.getId());
        var cpf = "12345678901";
        var requisicaoJson = """
                {
                    "cpfAssociado": "%s",
                    "opcao": "SIM"
                }
                """.formatted(cpf);

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.RECURSO_VOTOS_V1.replace("{sessaoId}", sessao.getId().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.sessaoId").value(sessao.getId().toString()))
                .andExpect(jsonPath("$.cpfAssociado").value(cpf))
                .andExpect(jsonPath("$.opcao").value("SIM"))
                .andExpect(jsonPath("$.dataHoraCriacao").isNotEmpty());

        var votosSalvos = repositorioVotoJpa.findAll();
        assertEquals(1, votosSalvos.size(), "Deve haver exatamente um voto no banco");
        assertEquals(cpf, votosSalvos.getFirst().getCpfAssociado());
        assertEquals(VotoOpcao.SIM, votosSalvos.getFirst().getOpcao());
    }

    @Test
    @DisplayName("Deve registrar voto com opção NÃO com sucesso")
    void deveRegistrarVotoComOpcaoNao() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para voto negativo");
        var sessao = criarSessaoAbertaNoBanco(pauta.getId());
        var cpf = "98765432100";
        var requisicaoJson = """
                {
                    "cpfAssociado": "%s",
                    "opcao": "NAO"
                }
                """.formatted(cpf);

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.RECURSO_VOTOS_V1.replace("{sessaoId}", sessao.getId().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.opcao").value("NAO"));

        var votosSalvos = repositorioVotoJpa.findAll();
        assertEquals(VotoOpcao.NAO, votosSalvos.getFirst().getOpcao());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando CPF não está no padrão")
    void deveRetornarErroQuandoCpfInvalido() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para teste CPF inválido");
        var sessao = criarSessaoAbertaNoBanco(pauta.getId());
        var cpfInvalido = "123";
        var requisicaoJson = """
                {
                    "cpfAssociado": "%s",
                    "opcao": "SIM"
                }
                """.formatted(cpfInvalido);

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.RECURSO_VOTOS_V1.replace("{sessaoId}", sessao.getId().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isBadRequest());

        assertEquals(0, repositorioVotoJpa.count(), "O banco deve permanecer vazio após erro de validação");
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando CPF não for informado")
    void deveRetornarErroQuandoCpfVazio() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para teste CPF vazio");
        var sessao = criarSessaoAbertaNoBanco(pauta.getId());
        var requisicaoJson = """
                {
                    "opcao": "SIM"
                }
                """;

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.RECURSO_VOTOS_V1.replace("{sessaoId}", sessao.getId().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isBadRequest());

        assertEquals(0, repositorioVotoJpa.count(), "O banco deve permanecer vazio após erro de validação");
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando opção de voto não for informada")
    void deveRetornarErroQuandoOpcaoVazia() throws Exception {
        // Arrange
        var pauta = criarPautaNoBanco("Pauta para teste opção vazia");
        var sessao = criarSessaoAbertaNoBanco(pauta.getId());
        var requisicaoJson = """
                {
                    "cpfAssociado": "12345678901"
                }
                """;

        // Act & Assert
        mockMvc.perform(post(ApiConstantes.RECURSO_VOTOS_V1.replace("{sessaoId}", sessao.getId().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoJson))
                .andExpect(status().isBadRequest());

        assertEquals(0, repositorioVotoJpa.count(), "O banco deve permanecer vazio após erro de validação");
    }

    private PautaEntidade criarPautaNoBanco(String titulo) {
        var pauta = PautaEntidade.builder()
                .id(UUID.randomUUID())
                .titulo(titulo)
                .descricao("Descrição Teste")
                .dataHoraCriacao(Instant.now())
                .build();

        return repositorioPautaJpa.save(pauta);
    }

    private SessaoEntidade criarSessaoAbertaNoBanco(UUID pautaId) {
        var agora = Instant.now();
        var sessao = SessaoEntidade.builder()
                .id(UUID.randomUUID())
                .pautaId(pautaId)
                .dataHoraInicio(agora)
                .dataHoraTermino(agora.plus(1, ChronoUnit.MINUTES))
                .status(SessaoStatus.ABERTA)
                .totalVotos(0)
                .totalSim(0)
                .totalNao(0)
                .build();

        return repositorioSessaoJpa.save(sessao);
    }
}

