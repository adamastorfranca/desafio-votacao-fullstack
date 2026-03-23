# Desafio Votação Cooperativa - Frontend

![React](https://img.shields.io/badge/React-19-blue?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8.0-purple?logo=vite&logoColor=white)
![Material UI](https://img.shields.io/badge/Material--UI-7.3-blue?logo=mui&logoColor=white)
![Recharts](https://img.shields.io/badge/Recharts-3.8-lightgrey)

---

## 📋 Sobre o Projeto

O **Frontend** do "Desafio Votação Cooperativa" é uma Single Page Application (SPA) construída com as mais modernas tecnologias web para prover uma interface administrativa de nível corporativo e financeiro. Ele consome a API REST desenvolvida em Java (Spring Boot) e fornece uma experiência fluida para a gestão de pautas de assembleias, abertura de sessões e recepção de votos.

### Diferenciais Técnicos

✅ **Arquitetura Modular** — Componentização clara com divisão estrita de responsabilidades (Componentes, Páginas, Serviços, Tipos).  
✅ **Design Corporativo e Dashboard Financeiro** — Filtros avançados, paginação e cartões de resumo integrados com bibliotecas gráficas.  
✅ **Feedback em Tempo Real e Tratamento de Erros** — Interceptadores Axios para captura global de exceções, com integração limpa do React Toastify.  
✅ **Tipagem Estrita (End-to-End)** — Interfaces TypeScript que espelham exatamente os DTOs trafegados pelo backend.  
✅ **Nomenclatura Inteiramente em Português** — Alinhamento total com as regras de negócio e contratos de integração.  

---

## 🏗️ Arquitetura e Estrutura de Pastas

```plaintext
frontend/
│
├── public/                  ← Assets estáticos
├── src/
│   ├── assets/              ← Recursos da aplicação (imagens, ícones)
│   ├── components/          ← Componentes visuais reutilizáveis (MUI)
│   │   ├── Layout/          ← Estrutura base da página (Header, Sidebar)
│   │   └── ...              ← Modais, Gráficos (Recharts), Tabelas, etc.
│   ├── pages/               ← Ecrãs principais (PainelPrincipal, PaginaPautas)
│   ├── services/            ← Configuração do Axios e chamadas para a API
│   ├── styles/              ← Customizações de estilo (CSS/Theme)
│   ├── types/               ← Interfaces TypeScript (DTOs do Backend)
│   └── utils/               ← Funções utilitárias (Formatadores de datas, etc)
│
├── package.json             ← Dependências e scripts
├── tsconfig.json            ← Configuração TypeScript
└── vite.config.ts           ← Configuração do bundler Vite
```

### Padrões Implementados

| Padrão | Localização | Objetivo |
|--------|------------|----------|
| **Componentização** | `components/` | Reutilização de blocos UI baseados em Material UI |
| **Páginas (Views)**| `pages/` | Agrupamento lógico de componentes de página |
| **Serviços (API)** | `services/` | Centralização e isolamento das chamadas HTTP via Axios |
| **Data Transfer Objects** | `types/` | Definição rigorosa de contratos da API externa |
| **Interceptadores HTTP** | `services/api.ts` | Captura global e tratamento padronizado de erros de rede e Regras de Negócio |

---

## 🛠️ Stack Tecnológica

| Tecnologia | Função |
|------------|--------|
| **React 19** | Biblioteca core para construção de interfaces. |
| **TypeScript** | Superset JavaScript adicionando tipagem estática. |
| **Vite** | Bundler extremamente rápido para ambiente de desenvolvimento. |
| **Material UI (MUI) v7** | Biblioteca de componentes visuais, base do Design System. |
| **React Router DOM** | Roteamento Client-Side (SPA). |
| **Axios** | Cliente HTTP baseado em Promises para comunicação com a API. |
| **Recharts** | Biblioteca para construção inteligente e declarativa de gráficos no painel de relatórios. |
| **React Toastify** | Feedbacks visuais e notificações de ações/erros para o utilizador. |

---

## 📋 Pré-requisitos

Para executar o projeto frontend, necessitará de:

- **Node.js** (versão 20+ recomendada)
  - Verificar: `node -v`
- **NPM** (Gerenciador de pacotes do Node, instalado junto com o Node.js)
  - Verificar: `npm -v`

E claro, o **Backend** estar em execução paralelamente para consumir os dados (por norma, escutando em `http://localhost:8080`).

---

## 🚀 Como Executar Localmente

### 1️⃣ Instalar as Dependências

Na raiz do diretório `frontend`, execute:

```bash
npm install
```

### 2️⃣ Configurar Variáveis de Ambiente (Opcional)

Geralmente o Vite utiliza as variáveis locais. Em caso de necessidade, poderá definir o caminho da API. O serviço base padrão está configurado em `/src/services/api.ts`, consumindo de `http://localhost:8080`.

### 3️⃣ Iniciar o Servidor de Desenvolvimento

```bash
npm run dev
```

A aplicação será exposta localmente. Geralmente em: **http://localhost:5173**

---

## 📦 Como Compilar para Produção

Para gerar a build estática de produção com minificação de arquivos:

```bash
npm run build
```

Os arquivos processados serão alocados no diretório estático `/dist`. Para testar a versão gerada via preview:

```bash
npm run preview
```

---

## 🤝 Integração com o Backend

A aplicação frontend acede nativamente às rotas configuradas no backend Spring Boot do mesmo ecossistema:

- **`GET /api/v1/pautas`** - Listagem avançada com suporte a paginação e filtros de dashboard.
- **`POST /api/v1/pautas`** - Criação de uma pauta cooperativa.
- **`POST /api/v1/pautas/{id}/sessoes`** - Abertura de sessões com durações parametrizáveis.
- **`POST /api/v1/sessoes/{id}/votos`** - Registro dos votos dos associados, lidando diretamente com as negações atreladas às checagens externas do CPF.
- **`GET /api/v1/sessoes/{id}/resultado`** - Agrupamento e mapeamento dos resultados da votação da referida sessão.

**Mecanismos de Resiliência Frontend:**
O Axios intercepta respostas `HTTP 400` ou `HTTP 404` originárias da indisponibilidade do CPF no integrador externo e exibe imediatamente um Toast Error informando ao usuário `CPF inválido ou não apto a votar`, impossibilitando as engrenagens de interface engasgarem perante as quebras.
