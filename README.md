# Desafio Votação Cooperativa

![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green?logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7-black?logo=apache-kafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.2-red?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-success)

---

## 📋 Sobre o Projeto

**Desafio Votação Cooperativa** é uma API REST de **alta performance** e **resiliência** para gerenciar sessões de votação em assembleias cooperativas. A solução foi desenvolvida como resposta ao desafio técnico backend da **SICREDI**, incorporando boas práticas de arquitetura, segurança e escalabilidade.

### Objetivo Principal

Gerenciar pautas e sessões de votação em um ambiente cooperativo, onde cada associado (identificado por CPF) possui um voto por pauta, com suporte simultâneo para **centenas de milhares de votos** por sessão.

### Diferencias Técnicos

✅ **Arquitetura Hexagonal (Ports & Adapters)** — Isolamento total entre domínio e infraestrutura  
✅ **Performance Extrema** — Pipeline assíncrono (API → Redis → Kafka → Batch Insert)  
✅ **Resiliência** — Circuit Breaker (Resilience4j), Cache distribuído (Redis), Retry automático  
✅ **Multi-camadas de Validação** — CPF, sessão, integridade de votos  
✅ **Mensageria em Tempo Real** — Apache Kafka para processamento e publicação de resultados  
✅ **Testes Robustos** — Testes de integração com Testcontainers (BD, Kafka, Redis reais)  
✅ **Documentação Interativa** — Swagger/OpenAPI UI integrada  

---

## 🏗️ Arquitetura

### Visão Geral

A arquitetura segue o padrão **Hexagonal (Ports and Adapters)**, dividindo o projeto em três camadas lógicas:

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRAESTRUTURA                           │
│  (Spring, Controllers, Repositories, Kafka, Cache)         │
├─────────────────────────────────────────────────────────────┤
│                         CORE                                │
│        (Casos de Uso, Domínio, Regras de Negócio)         │
├─────────────────────────────────────────────────────────────┤
│            EXTERNALS (BD, APIs, Mensageria)               │
└─────────────────────────────────────────────────────────────┘
```

### Estrutura de Pacotes

```plaintext
br/com/adamastor/votacao
│
├── core/
│   ├── dominio/
│   │   ├── modelo/              ← Entidades do domínio (Pauta, Sessão, Voto)
│   │   ├── excecao/             ← Exceções de negócio
│   │   └── validador/           ← Regras de validação de domínio
│   │
│   └── aplicacao/
│       ├── porta/
│       │   ├── entrada/         ← Driving Ports (Interfaces dos Casos de Uso)
│       │   └── saida/           ← Driven Ports (Banco, Kafka, APIs externas)
│       ├── caso_uso/            ← Implementação da lógica
│       └── dto/                 ← DTOs agnósticos de framework
│
└── infraestrutura/
    ├── configuracao/            ← Beans, Swagger, CORS
    │   └── bean/
    │
    ├── entrada/                 ← Adaptadores primários
    │   ├── rest/
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── mapper/
    │   │   └── manipulador/     ← Global Exception Handler
    │   └── mensageria/          ← Kafka Consumers
    │
    └── saida/                   ← Adaptadores secundários
        ├── persistencia/        ← PostgreSQL
        │   ├── entidade/
        │   ├── repositorio/
        │   ├── adaptador/
        │   └── mapper/
        ├── integracao/          ← APIs externas (CPF)
        │   ├── cliente/
        │   └── adaptador/
        └── mensageria/          ← Kafka Producers
```

### Fluxo de Performance (Bônus 3)

Para suportar centenas de milhares de votos simultâneos, implementamos um pipeline **totalmente assíncrono**:

```
┌─────────────────────────────────────────────────────────────┐
│  1. Cliente HTTP envia voto                                 │
│                                                              │
│     POST /votacao/sessoes/{id}/votos                       │
└──────────────┬──────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│  2. API (Controller) — Validação Rápida                    │
│     ✓ Sessão existe?                                       │
│     ✓ Associado já votou? (Redis Lock)                     │
│     ✓ Opção válida (Sim/Não)?                              │
└──────────────┬──────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│  3. Redis — Validação Distribuída                          │
│     ✓ Set de CPFs que já votaram por sessão               │
│     ✓ Resposta imediata (< 5ms)                            │
└──────────────┬──────────────────────────────────────────────┘
               │
               ▼ (Async)
┌─────────────────────────────────────────────────────────────┐
│  4. Kafka Producer — Enfileiramento                         │
│     ✓ Envio assíncrono para tópico "voto-topic"           │
│     ✓ Resposta HTTP 202 Accepted (Processando)            │
└──────────────┬──────────────────────────────────────────────┘
               │
               ▼ (Em Background)
┌─────────────────────────────────────────────────────────────┐
│  5. Kafka Consumer — Batch Processing                       │
│     ✓ Consome votos em lotes                               │
│     ✓ Validação final contra BD                            │
│     ✓ Persist em batch (< 1s por 10.000 votos)            │
└──────────────┬──────────────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│  6. Agendador — Fechamento Automático                       │
│     ✓ Detecta sessões expiradas                            │
│     ✓ Contabiliza votos                                    │
│     ✓ Publica resultado no Kafka                           │
└─────────────────────────────────────────────────────────────┘
```

**Benefício:** Desacoplamento total entre escrita de votos e persistência, permitindo suportar picos de carga sem degradação.

### Padrões Implementados

| Padrão | Localização | Objetivo |
|--------|------------|----------|
| **Hexagonal** | `core/` + `infraestrutura/` | Isolamento de domínio |
| **Adapter** | `infraestrutura/saida/` | Conversão entre domínio e frameworks |
| **Repository** | `infraestrutura/saida/persistencia/` | Abstração de dados |
| **DTO** | `infraestrutura/entrada/rest/dto/` | Contrato de API |
| **Circuit Breaker** | `infraestrutura/saida/integracao/` | Resiliência em APIs externas |
| **Mapper** | `infraestrutura/mapper/` | Conversão DTO ↔ Domínio (MapStruct) |

---

## 🛠️ Stack Tecnológica

### Backend

| Tecnologia | Versão | Função |
|------------|--------|--------|
| **Java** | 21 LTS | Linguagem (Virtual Threads para I/O) |
| **Spring Boot** | 3.4.0 | Framework web e container IoC |
| **Spring Data JPA** | 3.4.0 | ORM e persistência |
| **Spring Kafka** | 3.4.0 | Produtor/Consumidor de mensagens |
| **Spring Cache + Redis** | 3.4.0 | Cache distribuído e locks |
| **Spring Security (Web)** | 3.4.0 | Segurança (abstraída) |

### Armazenamento

| Tecnologia | Versão | Função |
|------------|--------|--------|
| **PostgreSQL** | 16 | Banco de dados relacional |
| **Redis** | 7.2 | Cache distribuído e sincronização |
| **Apache Kafka** | 3.7 | Broker de mensagens (Event Streaming) |

### Ferramentas & Utilitários

| Ferramenta | Versão | Função |
|-----------|--------|--------|
| **Maven** | 3.9+ | Build & Dependency Management |
| **Flyway** | 10+ | Migrações de BD (versionadas) |
| **Lombok** | 1.18.38 | Redução de boilerplate (getters, setters) |
| **MapStruct** | 1.5.5 | Mapping automático DTO ↔ Entidade |
| **Resilience4j** | 2.2.0 | Circuit Breaker, Retry, Bulkhead |
| **SpringDoc OpenAPI** | 2.6.0 | Documentação Swagger/OpenAPI |
| **Docker & Docker Compose** | Latest | Containerização e orquestração local |

### Testes

| Framework | Versão | Função |
|-----------|--------|--------|
| **JUnit 5** | 5.10+ | Framework de testes |
| **Mockito** | 5.0+ | Mocks e spies |
| **Testcontainers** | 1.20.1 | Testes de integração (BD, Kafka reais) |
| **Spring Boot Test** | 3.4.0 | Utilitários e `@SpringBootTest` |
| **Awaitility** | 4.0+ | Assertions assíncronas |

### Frontend

O sistema conta com um Dashboard Corporativo Financeiro desenvolvido em **React 19** e **TypeScript**, focando em UX/UI com **Material UI**. A aplicação é responsável pelo gerenciamento de pautas, abertura de sessões e recepção de votos.

| Tecnologia | Função |
|------------|--------|
| **React 19 + TypeScript** | Core SPA corporativa com tipagem estrita |
| **Material UI (MUI)** | Design System corporativo e responsivo |
| **Vite** | Build tool e dev server de altíssima performance |
| **Recharts / Axios** | Gráficos dinâmicos e comunicação HTTP |

👉 **[Consulte a documentação completa do Frontend aqui](./frontend/README.md)**

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 21 LTS** ou superior
  - Verificar: `java -version`
  - Download: [OpenJDK 21](https://jdk.java.net/21/) ou [Adoptium](https://adoptium.net/)

- **Docker & Docker Compose** (para infraestrutura)
  - Verificar: `docker --version` e `docker-compose --version`
  - Download: [Docker Desktop](https://www.docker.com/products/docker-desktop)

- **Maven 3.9+** (ou use o Maven Wrapper incluído)
  - Verificar: `mvn --version`
  - O projeto inclui `mvnw` / `mvnw.cmd` (Maven Wrapper)

- **Git** (para controle de versão)
  - Verificar: `git --version`

---

## 🚀 Como Executar

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/desafio-votacao.git
cd desafio-votacao
```

### 2️⃣ Subir a Infraestrutura (Docker Compose)

Inicie PostgreSQL, Redis e Kafka:

```bash
docker-compose up -d
```

Verifique o status:

```bash
docker-compose ps
```

Você deverá ver:

```
NAME                 IMAGE                      STATUS
votacao-db           postgres:16-alpine         Up (healthy)
votacao-redis        redis:7.2-alpine           Up (healthy)
votacao-kafka        apache/kafka:3.7.0         Up
```

### 3️⃣ Compilar o Projeto

Usando o Maven Wrapper (recomendado):

```bash
./mvnw clean install
```

Ou, se preferir Maven global:

```bash
mvn clean install
```

### 4️⃣ Executar a Aplicação

**Opção A: Via Maven**

```bash
./mvnw spring-boot:run
```

**Opção B: Via JAR compilado**

```bash
java -jar target/desafio-votacao-0.0.1-SNAPSHOT.jar
```

A aplicação iniciará em: **http://localhost:8080**

### 5️⃣ Verificar a Aplicação

- **Swagger UI (Documentação Interativa):**  
  http://localhost:8080/swagger-ui.html

- **OpenAPI JSON:**  
  http://localhost:8080/v3/api-docs

- **Health Check:**  
  http://localhost:8080/actuator/health

---

## 📡 Endpoints Principais

### Pauta

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/pautas` | Criar nova pauta |
| `GET` | `/api/v1/pautas` | Listar todas as pautas |
| `GET` | `/api/v1/pautas/{id}` | Obter pauta por ID |

**Exemplo:**

```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Aprovação de novo convênio",
    "descricao": "Votação para aceitar novo convênio de saúde"
  }'
```

### Sessão de Votação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/pautas/{id}/sessoes` | Abrir sessão (default 1 min) |
| `POST` | `/api/v1/pautas/{id}/sessoes?duracao=2` | Abrir sessão com duração (minutos) |
| `GET` | `/api/v1/sessoes/{id}` | Obter status da sessão |

**Exemplo:**

```bash
curl -X POST http://localhost:8080/api/v1/pautas/550e8400-e29b-41d4-a716-446655440000/sessoes \
  -H "Content-Type: application/json" \
  -d '{"duracao": 5}'
```

### Votos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/v1/sessoes/{id}/votos` | Registrar voto (Sim/Não) |

**Exemplo:**

```bash
curl -X POST http://localhost:8080/api/v1/sessoes/660f8400-e29b-41d4-a716-446655440000/votos \
  -H "Content-Type: application/json" \
  -d '{
    "cpfAssociado": "12345678901",
    "opcaoVoto": "SIM"
  }'
```

### Resultado

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/v1/sessoes/{id}/resultado` | Obter resultado da votação |

**Exemplo:**

```bash
curl -X GET http://localhost:8080/api/v1/sessoes/660f8400-e29b-41d4-a716-446655440000/resultado
```

---

## 🎁 Funcionalidades Extras (Bônus)

### 1. Integração com API Externa (Validação de CPF)

**Objetivo:** Verificar se o CPF do associado é válido e se está apto a votar.

**Implementação:**

- **Cliente Feign:** `IntegracaoCpfCliente`
- **URL:** `GET https://user-info.herokuapp.com/users/{cpf}`
- **Respostas:**
  - `200 OK` com `status: ABLE_TO_VOTE` → Associado pode votar
  - `200 OK` com `status: UNABLE_TO_VOTE` → Associado não pode votar
  - `404 NOT FOUND` → CPF inválido

**Resiliência:**

- **Circuit Breaker:** Se a API externa cair, o sistema não colapsa
- **Retry Automático:** 3 tentativas com backoff exponencial
- **Cache:** Resultados cacheados por 24 horas (Redis)
- **Fallback:** Em caso de falha, nega voto com mensagem clara

**Código de Exemplo:**

```java
@PostMapping("/{sessaoId}/votos")
public ResponseEntity<?> registrarVoto(
    @PathVariable UUID sessaoId,
    @Valid @RequestBody RegistrarVotoRequest request) {
    
    // Validação automática via Feign + Resilience4j
    var statusCpf = cpfIntegracaoService.validarCpf(request.getCpfAssociado());
    
    if (statusCpf != ABLE_TO_VOTE) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("CPF não apto a votar"));
    }
    
    var voto = registrarVotoCasoDeUso.executar(sessaoId, request);
    return ResponseEntity.status(202).body(voto);  // 202 Accepted (Async)
}
```

### 2. Mensageria de Resultados (Kafka)

**Objetivo:** Publicar resultado da votação em um tópico Kafka quando a sessão encerrar.

**Fluxo:**

1. Agendador verifica sessões expiradas a cada 10 segundos
2. Para cada sessão finalizada:
   - Contabiliza votos (SIM, NÃO)
   - Determina opção ganhadora
   - Publica mensagem no tópico `sessao-resultado-topic`

**Tópico Kafka:**

```json
{
  "idSessao": "660f8400-e29b-41d4-a716-446655440000",
  "idPauta": "550e8400-e29b-41d4-a716-446655440000",
  "totalVotos": 5432,
  "totalSim": 3200,
  "totalNao": 2232,
  "opcaoGanhadora": "SIM",
  "dataEncerramento": "2026-01-22T15:30:45Z"
}
```

**Consumer (para integração com outros sistemas):**

```bash
docker exec -it votacao-kafka \
  kafka-console-consumer.sh \
  --topic sessao-resultado-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

### 3. Versionamento de API

**Estratégia:** Versionamento via **URL Path** (`/api/v1/`, `/api/v2/`).

**Benefícios:**

- Evolução gradual da API
- Compatibilidade com clientes antigos
- Deprecação controlada de endpoints

**Exemplo:**

```java
@RestController
@RequestMapping("/api/v1")
public class PautaControllerV1 { ... }

@RestController
@RequestMapping("/api/v2")
public class PautaControllerV2 { ... }
```

---

## ✅ Como Testar

### Executar Todos os Testes

```bash
./mvnw clean verify
```

Isso executará:

- **Testes unitários** (src/test/java)
- **Testes de integração** (Testcontainers com BD, Kafka reais)
- **Análise de cobertura de código**

### Testes Específicos

**Apenas testes unitários:**

```bash
./mvnw test
```

**Apenas testes de integração:**

```bash
./mvnw verify -DskipUnitTests
```

**Teste específico:**

```bash
./mvnw test -Dtest=CriarPautaCasoDeUsoImplTest
```

### Verificar Cobertura de Testes

Após rodar `mvnw verify`, veja o relatório:

```bash
open target/site/jacoco/index.html  # macOS
start target/site/jacoco/index.html # Windows
```

---

## 📊 Monitoramento e Observabilidade

### Endpoints de Saúde (Actuator)

```bash
curl http://localhost:8080/actuator/health
```

Resposta:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "database": "PostgreSQL" },
    "kafka": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

### Logs

Logs estruturados são gravados em `logs/` (configurável em `application.yml`).

```bash
tail -f logs/aplicacao.log
```

### Métricas (Prometheus)

Endpoint disponível em: `http://localhost:8080/actuator/metrics`

---

## 🔐 Segurança

### Considerações Atuais

- ✅ **Segurança abstraída** conforme requisito (assumir autorizado)
- ✅ **Validação de entrada** (CPF, opções de voto)
- ✅ **Circuit Breaker** contra APIs não-confiáveis
- ✅ **Rate Limiting** implementado (recomendado em produção)

### Para Produção

- Implementar **OAuth2/JWT** no Spring Security
- Adicionar **HTTPS** (TLS/SSL)
- Usar **WAF** (Web Application Firewall)
- Configurar **CORS** adequadamente
- Auditar acessos com **AOP**

---

## 📁 Estrutura de Diretórios Importantes

```plaintext
desafio-votacao/
│
├── frontend/                    ← Dashboard Corporativo (React, TS, MUI)
│
├── src/main/java/br/com/adamastor/votacao/
│   ├── core/                    ← Lógica pura (Domínio)
│   └── infraestrutura/          ← Adaptadores (Spring, BD, etc.)
│
├── src/main/resources/
│   ├── db/migration/            ← Scripts SQL (Flyway)
│   ├── application.yml          ← Configuração geral
│   ├── application-local.yaml   ← Profile local
│   └── application-prod.yml     ← Profile produção
│
├── src/test/java/               ← Testes automatizados
│   └── br/com/adamastor/votacao/
│       ├── core/                ← Testes de domínio
│       └── infraestrutura/      ← Testes de integração
│
├── docker-compose.yml           ← Infraestrutura (Postgres, Redis, Kafka)
├── Dockerfile                   ← Imagem da aplicação
├── pom.xml                      ← Dependências Maven
└── README.md                    ← Este arquivo
```

---

## 🔄 Fluxo de Desenvolvimento

### 1. Feature Branch

```bash
git checkout -b feat/nova-funcionalidade
```

### 2. Implementar & Testar

```bash
./mvnw clean verify
```

### 3. Commit com Conventional Commits

```bash
git commit -m "feat: implementação de criação de sessão de votação"
git commit -m "test: testes para validação de CPF"
git commit -m "docs: atualização do README"
git commit -m "fix: correção em contabilização de votos"
```

### 4. Push & Pull Request

```bash
git push origin feat/nova-funcionalidade
```

---

## 🐛 Troubleshooting

### Erro: "Connection to localhost:5432 refused"

**Causa:** PostgreSQL não está rodando.

**Solução:**

```bash
docker-compose up -d postgres-db
docker-compose logs postgres-db
```

### Erro: "Failed to connect to Kafka broker"

**Causa:** Kafka não iniciou.

**Solução:**

```bash
docker-compose down
docker-compose up -d kafka
sleep 10  # Aguardar inicialização
```

### Erro: "Redis connection timeout"

**Causa:** Redis não está acessível.

**Solução:**

```bash
docker-compose restart redis
redis-cli -h localhost ping  # Deve retornar PONG
```

### Porta 8080 já em uso

**Solução:**

```bash
# Encontre a aplicação usando a porta
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Inicie em porta diferente
java -Dserver.port=8081 -jar target/desafio-votacao-0.0.1-SNAPSHOT.jar
```

---

## 📚 Documentação Adicional

- **Guia de Arquitetura Hexagonal:** Consulte `docs/arquitetura.md` (se disponível)
- **API OpenAPI/Swagger:** http://localhost:8080/swagger-ui.html
- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Apache Kafka Guide:** https://kafka.apache.org/documentation/
- **PostgreSQL Docs:** https://www.postgresql.org/docs/

