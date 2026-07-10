# Stock Management

Sistema de controle de estoque e produção sob demanda, construído para resolver um problema real de operações: gestão de insumos, receitas de produtos configuráveis e alertas de gargalo em uma cadeia de manufatura distribuída (fábrica + unidade de embalagem externa).

O núcleo do sistema é um motor de **cálculo de capacidade produtiva**: cada produto tem uma receita de insumos com quantidades independentes, e o usuário define qual insumo é o gargalo esperado. A cada movimentação de estoque, o sistema recalcula a capacidade real de produção e identifica quando o insumo que **de fato** está limitando a produção diverge do gargalo assumido — um cenário comum em operações pequenas, onde a percepção de gargalo costuma ficar desatualizada conforme o mix de insumos muda.

## Stack

**Backend**
- Kotlin + Spring Boot 3 (Web, Data JPA, Validation)
- MySQL 8
- Flyway (versionamento de schema)
- JUnit 5 + Kotest
- Gradle (Kotlin DSL) / Java 21

**Frontend**
- React 18 + TypeScript
- Vite
- Material UI
- React Router + Axios
- Vitest + React Testing Library

**Infra**
- Deploy em Railway (backend e frontend como serviços independentes no mesmo repositório)
- MySQL gerenciado externamente, com o mesmo banco usado em desenvolvimento e produção

## Domínio

```
Insumo (1) ────< ProdutoInsumo >──── (1) Produto
   │                                      │
   │                                      └── insumoGargalo (referencia 1 insumo da própria receita)
   │
   └──< MovimentacaoEstoque (ENTRADA / SAIDA)

Produto (1) ────< Embalagem >──── (0..1) Envio
```

- **Insumo**: item físico com quantidade em estoque.
- **Produto**: um item final com uma receita de insumos (`ProdutoInsumo`) e um insumo definido como gargalo.
- **MovimentacaoEstoque**: log append-only de toda entrada/saída de insumo, usado como trilha de auditoria.
- **Embalagem**: registro de produção — ao ser criado, desconta automaticamente cada insumo da receita do produto, na proporção definida.
- **Envio**: agrupa embalagens pendentes em um lote fechado, preservando histórico e zerando a fila de "pendente" sem apagar dados.

## Decisões de arquitetura

| Decisão | Racional |
|---|---|
| Regra de capacidade isolada em serviço puro (`CapacidadeService`) | A lógica de identificar o insumo limitante não depende de banco nem de contexto Spring — é testável unitariamente, com inputs e outputs determinísticos, sem mocks. |
| `ddl-auto: validate` + Flyway | O schema nunca é alterado implicitamente pelo Hibernate. Toda mudança estrutural é uma migration versionada, revisável e reproduzível — essencial ao operar contra um banco de produção que não pode ser recriado a cada deploy. |
| DTOs explícitos na camada HTTP | Entidades JPA nunca são serializadas diretamente — evita acoplamento entre o modelo de persistência e o contrato de API, e evita problemas de serialização com proxies de lazy loading do Hibernate. |
| Exceções de domínio + `@RestControllerAdvice` central | Regras de negócio (estoque insuficiente, inconsistência de gargalo) são sinalizadas via exceções tipadas, tratadas em um único ponto — os controllers ficam livres de tratamento de erro repetido. |
| Vite + Vitest no lugar de CRA + Jest | CRA está em manutenção; Vitest mantém API compatível com Jest (`describe`/`it`/`expect`) com integração nativa ao bundler, sem configuração adicional de transpilação. |

## Funcionalidades

- CRUD completo de insumos e produtos, com receita configurável por produto (N insumos, quantidade independente por unidade).
- Definição de insumo-gargalo por produto, com validação de que o gargalo pertence à própria receita.
- Registro de entrada de estoque com recálculo automático de capacidade para **todos** os produtos que compartilham aquele insumo — não apenas o produto onde ele é o gargalo declarado.
- Alerta automático quando o insumo que realmente limita a produção diverge do gargalo assumido pelo usuário.
- Registro de embalagem com validação atômica de estoque: se qualquer insumo da receita for insuficiente para a quantidade solicitada, a operação é rejeitada por completo (sem baixa parcial), retornando o detalhamento de cada insumo faltante.
- Fechamento de lote de envio: agrupa toda produção pendente, zera a fila de pendências e preserva o histórico completo de cada envio.
- Dashboard com visão consolidada de estoque, produção pendente e histórico de envios.

## API

| Método | Rota | Descrição |
|---|---|---|
| `GET/POST` | `/api/insumos` | Lista / cria insumos |
| `PUT/DELETE` | `/api/insumos/{id}` | Atualiza / remove insumo |
| `GET/POST` | `/api/produtos` | Lista / cria produtos (receita + gargalo) |
| `PUT/DELETE` | `/api/produtos/{id}` | Atualiza / remove produto |
| `GET` | `/api/produtos/{id}/capacidade` | Capacidade de produção atual e insumo limitante |
| `POST` | `/api/estoque/entrada` | Registra entrada de insumo; retorna capacidade de todos os produtos afetados |
| `GET` | `/api/estoque/movimentacoes` | Histórico de movimentações |
| `POST` | `/api/embalagens` | Registra produção; desconta insumos ou rejeita com detalhamento de falta |
| `GET` | `/api/embalagens/pendentes` | Produção aguardando envio |
| `POST/GET` | `/api/envios` | Fecha lote de envio / histórico |

## Rodando localmente

**Pré-requisitos**: Java 21, Node 20+, uma instância MySQL acessível (local ou remota).

### Backend

```bash
cd backend
```

Configure as variáveis de ambiente (via IntelliJ Run Configuration, ou exportando no shell):

```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=stockmanagement
DB_USER=root
DB_PASSWORD=sua_senha
ALLOWED_ORIGINS=http://localhost:5173
```

Rode a classe principal (`StockManagementApplicationKt`) pela IDE, ou:

```bash
./gradlew bootRun
```

O Flyway aplica as migrations automaticamente na primeira execução.

### Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Acesse `http://localhost:5173`.

### Testes

```bash
# Backend
cd backend && ./gradlew test

# Frontend
cd frontend && npm run test
```

O teste mais relevante do projeto é `CapacidadeServiceTest`, que cobre o cenário central do domínio: identificar o insumo limitante real mesmo quando diverge do gargalo declarado, incluindo o caso de um insumo compartilhado por múltiplos produtos com receitas e gargalos independentes.

## Deploy

Backend e frontend são deployados como serviços independentes apontando para o mesmo repositório (Root Directory configurado por serviço), com o backend consumindo um banco MySQL externo via variáveis de ambiente — sem acoplamento entre o ciclo de vida da aplicação e o do banco de dados.

## Possíveis evoluções

- Autenticação (Spring Security + JWT)
- Paginação nos endpoints de listagem
- Cache/revalidação client-side (React Query) no lugar do fetch manual atual
- Exportação de histórico de envios (CSV/PDF)

---

**Autor**: Antonio Kerr
