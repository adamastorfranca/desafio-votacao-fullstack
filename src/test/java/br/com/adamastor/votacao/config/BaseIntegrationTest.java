package br.com.adamastor.votacao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    private static final boolean DOCKER_DISPONIVEL = verificarDockerDisponivel();

    static PostgreSQLContainer<?> postgres;
    static GenericContainer<?> redis;
    static KafkaContainer kafka;

    static {
        if (DOCKER_DISPONIVEL) {
            inicializarTestContainers();
        } else {
            log.warn("Docker não disponível. Usando configuração com banco H2 em-memória para testes.");
            inicializarBancoEmMemoria();
        }
    }

    private static boolean verificarDockerDisponivel() {
        try {
            Class.forName("org.testcontainers.dockerclient.DockerClientFactory");
            var dockerClient = org.testcontainers.DockerClientFactory.instance().client();
            dockerClient.infoCmd().exec();
            log.info("Docker detectado e disponível para testes.");
            return true;
        } catch (Exception e) {
            log.warn("Docker não está disponível: {}. Usando H2 em-memória para testes.", e.getClass().getSimpleName());
            return false;
        }
    }

    private static void inicializarTestContainers() {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("votacao_test")
                .withUsername("votacao_user")
                .withPassword("votacao_pass");

        redis = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379);

        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

        postgres.start();
        redis.start();
        kafka.start();
    }

    private static void inicializarBancoEmMemoria() {
        log.info("Configurando H2 em-memória para testes locais sem Docker.");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (DOCKER_DISPONIVEL) {
            configurarTestContainers(registry);
        } else {
            configurarH2EmMemoria(registry);
        }
    }

    private static void configurarTestContainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        log.info("TestContainers configurado: PostgreSQL em {}, Redis em {}:{}, Kafka em {}",
                postgres.getJdbcUrl(),
                redis.getHost(),
                redis.getFirstMappedPort(),
                kafka.getBootstrapServers());
    }

    private static void configurarH2EmMemoria(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:votacao_test;MODE=PostgreSQL");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.h2.console.enabled", () -> "true");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.flyway.enabled", () -> "true");

        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> "6379");

        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");

        log.info("H2 em-memória configurado para ambiente local sem Docker.");
    }
}