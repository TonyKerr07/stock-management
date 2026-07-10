# Stock Management — Controle de Estoque para Manufatura Doméstica

Sistema de gestão de estoque desenvolvido para resolver um problema real de operação:
o controle de insumos e produtos finais em um fluxo de **manufatura distribuída** entre
uma fábrica e uma unidade de embalagem terceirizada (neste caso, doméstica).

O sistema resolve um problema clássico de operações — **gestão de gargalo (bottleneck)
em cadeia de suprimentos** — aplicado em pequena escala: cada produto tem uma receita
configurável de insumos, e o usuário define qual insumo é o fator limitante da produção.
A cada movimentação de estoque, o sistema recalcula a capacidade produtiva e alerta
quando o insumo que *realmente* está travando a produção é diferente do que foi
originalmente definido como gargalo — um problema que, no mundo real, geralmente só é
percebido depois que a produção já parou.

## Índice

- [Arquitetura e decisões técnicas](#arquitetura-e-decisões-técnicas)
- [Stack](#stack)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [1. Clonar o repositório](#1-clonar-o-repositório)
- [2. Configurar o banco MySQL (HostGator)](#2-configurar-o-banco-mysql-hostgator)
- [3. Rodar o backend (IntelliJ)](#3-rodar-o-backend-intellij)
- [4. Rodar o frontend](#4-rodar-o-frontend)
- [5. Testes](#5-testes)
- [6. API — Endpoints](#6-api--endpoints)
- [7. Deploy no Railway](#7-deploy-no-railway)
- [Modelo de domínio](#modelo-de-domínio)
- [Possíveis evoluções](#possíveis-evoluções)

---

## Arquitetura e decisões técnicas

| Decisão | Justificativa |
|---|---|
| **Kotlin + Spring Boot** no backend | Null-safety em nível de linguagem elimina uma classe inteira de bugs de NPE em regras de negócio críticas (ex.: cálculo de capacidade); interoperabilidade total com o ecossistema Java/Spring. |
| **Flyway** para versionamento de schema | Migrations versionadas e auditáveis em vez de `ddl-auto: update` — essencial ao trabalhar com um banco de produção remoto (HostGator) que não pode ser recriado a cada deploy. |
| **`ddl-auto: validate`** em vez de `update`/`create` | O Hibernate nunca altera o schema sozinho; toda mudança estrutural passa por uma migration explícita, revisável e reversível. |
| **Camada de `CapacidadeService` isolada** | A regra de negócio mais importante do sistema (identificar o insumo limitante) foi extraída para um serviço puro, sem dependência de banco — o que permite testá-la unitariamente sem subir contexto Spring nem banco H2. |
| **Vite em vez de Create React App** | CRA está em modo de manutenção; Vite é o padrão atual do ecossistema React (build ~10x mais rápido, HMR nativo). |
| **Vitest em vez de Jest** | API 100% compatível com Jest (`describe`/`it`/`expect`), mas com integração nativa ao Vite (sem necessidade de configurar Babel/ts-jest separadamente). |
| **DTOs explícitos (não expor entidades JPA)** | Entidades JPA nunca trafegam na camada HTTP — evita problemas de serialização com proxies do Hibernate (lazy loading) e desacopla o contrato de API do modelo de persistência. |
| **MySQL remoto (HostGator) tanto em dev quanto em produção** | Elimina divergência de ambiente (dialeto SQL, collation, tipos) entre local e produção — o mesmo banco (ou um clone dele) é usado nas duas pontas. |

## Stack

**Backend**
- Kotlin 1.9 + Spring Boot 3.3 (Web, Data JPA, Validation)
- MySQL 8 (HostGator, acesso remoto)
- Flyway (versionamento de schema)
- JUnit 5 + Kotest (testes)
- Gradle (Kotlin DSL)
- Java 21 (toolchain)

**Frontend**
- React 18 + TypeScript
- Vite (build e dev server)
- Material UI (MUI) v6
- React Router
- Axios
- Vitest + React Testing Library (testes)

**Infra**
- Railway (deploy de backend e frontend)
- MySQL do HostGator (banco único, usado local e em produção)

---

## Estrutura do projeto

```
stock-management/
├── backend/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── kotlin/com/kerr/stockmanagement/
│       │   │   ├── domain/        # Entidades JPA
│       │   │   ├── repository/    # Spring Data JPA
│       │   │   ├── dto/           # Contratos de entrada/saída da API
│       │   │   ├── service/       # Regras de negócio (inclui CapacidadeService)
│       │   │   ├── controller/    # Endpoints REST
│       │   │   ├── mapper/        # Entidade -> DTO
│       │   │   ├── exception/     # Exceções de negócio + handler global
│       │   │   └── config/        # CORS, etc.
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/  # Scripts Flyway (V1__init.sql, ...)
│       └── test/
│           ├── kotlin/...         # Testes (Kotest + JUnit)
│           └── resources/application-test.yml  # perfil de teste (H2)
└── frontend/
    └── src/
        ├── api/client.ts          # Instância do Axios
        ├── types/index.ts         # Tipos TS espelhando os DTOs do backend
        ├── components/            # Layout, AlertaGargalo
        ├── pages/                 # Dashboard, Insumos, Produtos, Entrada, Embalagem
        └── tests/                 # Testes Vitest + RTL
```

---

## Pré-requisitos

- **Java 21** (você já tem — confirmado)
- **IntelliJ IDEA** (Community ou Ultimate)
- **Node.js 24** (você já tem — confirmado, `v24.16.0`)
- Uma conta ativa na **HostGator** com acesso ao **cPanel**
- Uma conta no **GitHub**
- Uma conta no **Railway** (pode criar com login do GitHub, na hora do deploy)

---

## 1. Clonar o repositório

Você já criou o repositório `stock-management` no GitHub e clonou localmente. Agora:

1. Extraia o conteúdo do `.zip` que eu vou te enviar **dentro** da pasta que você clonou
   (`stock-management/`), de forma que o resultado final seja:

```
stock-management/
├── .gitignore
├── README.md
├── backend/
└── frontend/
```

2. No terminal, dentro da pasta `stock-management`, confirme que o Git está enxergando os arquivos:

```bash
git status
```

3. Suba o código para o GitHub:

```bash
git add .
git commit -m "chore: estrutura inicial do projeto (backend Kotlin + frontend React)"
git push origin main
```

> Se o Git pedir para configurar seu nome/e-mail, rode antes:
> `git config --global user.name "Antonio Kerr"` e
> `git config --global user.email "seu-email@exemplo.com"`

---

## 2. Configurar o banco MySQL (HostGator)

### 2.1 Criar o banco de dados no cPanel

1. Acesse o **cPanel** da HostGator (geralmente em `https://seudominio.com/cpanel` ou pelo link que a HostGator te enviou).
2. Procure a seção **"Bancos de Dados"** e clique em **"Bancos de Dados MySQL"** (MySQL® Databases).
3. Em **"Criar Novo Banco de Dados"**, digite: `stockmanagement` e clique em **Criar Banco de Dados**.
   - Repare que o cPanel **sempre prefixa** o nome do banco com o seu usuário de hospedagem, por exemplo:
     `seuusuario_stockmanagement`. **Anote esse nome completo** — é ele que você vai usar na conexão.
4. Desça até **"Usuários MySQL®"** → **"Adicionar Novo Usuário"**:
   - Nome de usuário: `stockuser` (também vai virar `seuusuario_stockuser`)
   - Senha: gere uma senha forte (o próprio cPanel tem um gerador — use-o e **guarde essa senha**)
5. Descendo mais, em **"Adicionar Usuário ao Banco de Dados"**:
   - Selecione o usuário que você acabou de criar e o banco `stockmanagement`
   - Clique em **Adicionar**
   - Na tela de privilégios, marque **"ALL PRIVILEGES"** e confirme

Ao final você deve ter anotado três coisas:
```
Nome do banco:   seuusuario_stockmanagement
Usuário:         seuusuario_stockuser
Senha:           (a que você gerou)
```

### 2.2 Liberar acesso remoto (Remote MySQL)

Por padrão, a HostGator só permite conexões ao MySQL vindas de dentro do próprio servidor.
Para conectar do seu computador (e depois do Railway), você precisa liberar acesso remoto:

1. No cPanel, procure **"Remote MySQL®"**.
2. Em **"Add Access Host"**, você tem duas opções:
   - **Opção recomendada para começar a testar**: adicionar `%` (permite qualquer IP conectar,
     desde que saiba usuário/senha corretos). É a forma mais simples de garantir que tanto o seu
     computador quanto o Railway (que usa IPs dinâmicos) consigam conectar.
   - **Opção mais restritiva**: adicionar o IP fixo do seu computador (descubra em
     [whatismyipaddress.com](https://whatismyipaddress.com)) — mas isso vai quebrar assim que
     você mudar de rede (wi-fi de outro lugar, 4G, etc.) ou quando o Railway trocar de IP.
3. Clique em **Add**.

> ⚠️ **Nota de segurança**: liberar `%` significa que qualquer pessoa no mundo pode *tentar*
> se conectar ao seu MySQL (mas só entra se souber usuário e senha corretos). Para um projeto
> pessoal/portfólio isso é uma prática aceitável e comum, mas evite reutilizar essa senha em
> outros lugares, e não deixe usuário/senha em nenhum arquivo commitado no Git — isso é o que
> o `.gitignore` do projeto já protege.

### 2.3 Descobrir o host de conexão

Normalmente o host de conexão remota da HostGator é o **próprio domínio da sua hospedagem**
(ex.: `seudominio.com`) ou um endereço tipo `gatorXXXX.hostgator.com`, que aparece na mesma
tela de **Remote MySQL** ou em **"Informações da Conta"** no cPanel. A porta padrão é `3306`.

Se tiver dúvida de qual host usar, procure no cPanel por **"Server Information"** — lá aparece
o hostname exato do servidor onde seu banco está hospedado.

---

## 3. Rodar o backend (IntelliJ)

1. Abra o **IntelliJ IDEA** → **File > Open** → selecione a pasta `stock-management/backend`.
2. O IntelliJ vai detectar o `build.gradle.kts` e perguntar se você confia no projeto
   (**Trust Project**) — clique em confiar.
3. Aguarde a importação do Gradle (primeira vez demora um pouco, baixa as dependências).
   Você acompanha o progresso na barra inferior direita.
4. Se o IntelliJ perguntar sobre o **Gradle JVM**, selecione o **Java 21** que você já tem instalado.

### 3.1 Configurar as variáveis de ambiente (conexão com o MySQL)

O backend lê a conexão com o banco via variáveis de ambiente (isso é o que permite usar o
**mesmo código** local e em produção, só trocando os valores). Configure assim:

1. No canto superior direito do IntelliJ, clique no menu de **Run Configurations** (ao lado do
   botão verde de play) → **Edit Configurations...**
2. Clique em **+** → **Application** (Kotlin).
3. Em **"Main class"**, procure e selecione `com.kerr.stockmanagement.StockManagementApplicationKt`.
4. Em **"Environment variables"**, clique no ícone de pasta e adicione, uma por linha:

```
DB_HOST=seudominio.com
DB_PORT=3306
DB_NAME=seuusuario_stockmanagement
DB_USER=seuusuario_stockuser
DB_PASSWORD=sua_senha_gerada
ALLOWED_ORIGINS=http://localhost:5173
```

5. Dê um nome para essa configuração, por exemplo `StockManagement (local)`, e clique em **OK**.
6. Clique no botão verde ▶️ **Run** — o Spring Boot vai subir na porta `8080`, o Flyway vai
   criar automaticamente todas as tabelas no seu banco remoto na primeira execução.

Se tudo der certo, você verá no console algo como:
```
Started StockManagementApplicationKt in X.XXX seconds
```

Para confirmar que a API está de pé, acesse no navegador: `http://localhost:8080/api/insumos`
— deve retornar `[]` (lista vazia, já que ainda não cadastrou nada).

> **Dica**: se aparecer erro de conexão recusada com o MySQL, volte no item 2.2 e confirme que
> o Remote MySQL está liberado, e que host/usuário/senha/nome do banco estão exatamente iguais
> ao que está no cPanel (copie e cole, não digite de cabeça).

---

## 4. Rodar o frontend

O frontend é independente do IntelliJ — roda por terminal (pode ser o terminal integrado do
próprio IntelliJ, `Alt+F12` ou o ícone de terminal na barra inferior).

1. Abra um terminal na pasta `frontend`:

```bash
cd stock-management/frontend
```

2. Copie o arquivo de exemplo de variáveis de ambiente:

```bash
cp .env.example .env
```

O `.env` já vem apontando para `http://localhost:8080/api`, que é onde o backend estará
rodando localmente. Não precisa mudar nada para rodar local.

3. Instale as dependências (só precisa fazer isso uma vez, ou quando o `package.json` mudar):

```bash
npm install
```

4. Rode o servidor de desenvolvimento:

```bash
npm run dev
```

5. Acesse **http://localhost:5173** no navegador. Com o backend já rodando (passo 3), a tela
   de Dashboard deve carregar normalmente.

### Rodando os dois juntos, sem Docker

Você não precisa de Docker Compose para isso — é só manter duas janelas abertas:

- **Janela 1 (IntelliJ)**: backend rodando na porta `8080` (via botão Run, como configurado acima)
- **Janela 2 (terminal)**: frontend rodando na porta `5173` (via `npm run dev`)

O frontend (porta 5173) faz requisições para o backend (porta 8080) via Axios, e o `CorsConfig`
do backend já está liberando essa origem (`http://localhost:5173`) por padrão.

---

## 5. Testes

### Backend (JUnit + Kotest)

Pelo IntelliJ: clique com o botão direito na pasta `backend/src/test` → **Run Tests**.

Ou pelo terminal:
```bash
cd backend
./gradlew test    # ou, se não tiver o wrapper, use o Gradle do próprio IntelliJ (aba Gradle lateral)
```

O teste mais importante do projeto é o `CapacidadeServiceTest`, que valida a regra de negócio
central: identificar corretamente qual insumo está limitando a produção, incluindo o cenário
descrito por você (produto com múltiplos insumos, gargalo definido vs. gargalo real).

### Frontend (Vitest + React Testing Library)

```bash
cd frontend
npm run test
```

---

## 6. API — Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/insumos` | Lista todos os insumos |
| `POST` | `/api/insumos` | Cria um insumo |
| `PUT` | `/api/insumos/{id}` | Atualiza um insumo |
| `DELETE` | `/api/insumos/{id}` | Remove um insumo |
| `GET` | `/api/produtos` | Lista todos os produtos (com receita) |
| `POST` | `/api/produtos` | Cria um produto (nome, receita, insumo gargalo) |
| `PUT` | `/api/produtos/{id}` | Atualiza um produto |
| `DELETE` | `/api/produtos/{id}` | Remove um produto |
| `GET` | `/api/produtos/{id}/capacidade` | Retorna a capacidade de produção atual e o insumo limitante |
| `POST` | `/api/estoque/entrada` | Registra entrada de insumo (retorna alertas de capacidade) |
| `GET` | `/api/estoque/movimentacoes` | Histórico de todas as movimentações |
| `POST` | `/api/embalagens` | Registra embalagem de produto (desconta insumos automaticamente) |
| `GET` | `/api/embalagens/pendentes` | Lista embalagens ainda não enviadas |
| `POST` | `/api/envios` | Fecha o envio atual (zera pendentes, mantém histórico) |
| `GET` | `/api/envios` | Histórico de envios |
| `GET` | `/api/envios/{id}` | Detalhe de um envio específico |

---

## 7. Deploy no Railway

Quando estiver tudo funcionando local, o deploy segue esta ordem:

1. Acesse [railway.app](https://railway.app) e crie uma conta (pode usar login do GitHub).
2. Clique em **New Project** → **Deploy from GitHub repo** → selecione `stock-management`.
3. O Railway vai tentar detectar automaticamente um único serviço. Você vai configurar **dois
   serviços** dentro do mesmo projeto, apontando para pastas diferentes do mesmo repositório:

### Serviço 1 — Backend
- Em **Settings** do serviço, defina **Root Directory**: `backend`
- Railway detecta o Gradle automaticamente e usa `./gradlew bootRun` (ou build + jar) como comando de start
- Em **Variables**, adicione as mesmas variáveis do IntelliJ:
  ```
  DB_HOST=seudominio.com
  DB_PORT=3306
  DB_NAME=seuusuario_stockmanagement
  DB_USER=seuusuario_stockuser
  DB_PASSWORD=sua_senha_gerada
  ALLOWED_ORIGINS=https://SEU-FRONTEND.up.railway.app
  ```
- Railway define automaticamente a variável `PORT` — o `application.yml` já está preparado
  para isso (`server.port: ${PORT:8080}`).
- Depois do primeiro deploy, copie a URL pública gerada (algo como
  `https://stock-management-backend-production.up.railway.app`).

### Serviço 2 — Frontend
- Em **Settings**, defina **Root Directory**: `frontend`
- Build command: `npm install && npm run build`
- Start command: `npm run preview -- --host 0.0.0.0 --port $PORT`
- Em **Variables**, adicione:
  ```
  VITE_API_URL=https://stock-management-backend-production.up.railway.app/api
  ```
  (usando a URL real do backend que você copiou no passo anterior)

4. Depois do deploy do frontend, volte no serviço do **backend** e atualize `ALLOWED_ORIGINS`
   com a URL real gerada para o frontend, e faça o redeploy do backend (o Railway costuma
   ter um botão **Redeploy**).

### Sobre o banco de dados no deploy

Não é necessário (nem recomendado) criar um novo banco no Railway — o mesmo banco MySQL do
HostGator, já com o **Remote MySQL** liberado (passo 2.2), é usado tanto local quanto em
produção. Isso significa que os dados que você cadastrar em produção vão persistir e podem
ser revisitados depois, mesmo que você redeploy o Railway ou desligue seu computador.

---

## Modelo de domínio

```
Insumo (1) ────< ProdutoInsumo >──── (1) Produto
   │                                      │
   │                                      └── insumoGargalo (aponta para 1 Insumo da receita)
   │
   └──< MovimentacaoEstoque (ENTRADA / SAIDA)

Produto (1) ────< Embalagem >──── (0..1) Envio
```

- **Insumo**: item físico com quantidade em estoque (toalha, embalagem, etiqueta, papelão...)
- **Produto**: um item final com uma receita (`ProdutoInsumo`) de insumos + um insumo gargalo
- **MovimentacaoEstoque**: log de toda entrada/saída de insumo (auditoria completa)
- **Embalagem**: registro de "embalei X unidades do produto Y", que desconta a receita do estoque
- **Envio**: agrupa embalagens pendentes em um lote quando você avisa que enviou para a fábrica —
  preserva o histórico e zera a contagem "pendente" no dashboard

## Possíveis evoluções

- Autenticação (Spring Security + JWT) — hoje a API é aberta, adequada para uso pessoal
- Paginação nos endpoints de listagem, caso o histórico cresça muito
- WebSocket para atualizar o dashboard em tempo real entre você e sua esposa (se usarem ao mesmo tempo)
- React Query no frontend para cache e revalidação automática (hoje é `useEffect` + `useState` simples)
- Exportar histórico de envios em CSV/PDF para prestação de contas com a fábrica

---

Desenvolvido por **Antonio Kerr**.
