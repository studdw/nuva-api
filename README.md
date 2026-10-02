# NUVA — Guardiões da Luz · API RESTful (Java)

API em **Java 17 + Spring Boot 3 + JDBC (Oracle)** que dá suporte ao front-end React da NUVA: missões sustentáveis, validação por vídeo (IA simulada), SoulCoins, conversão em desconto na conta de energia, ligas comunitárias e contato.

- **Front-end:** https://nuva-guardioes-da-luz.vercel.app
- **API (Render):** https://nuva-api.onrender.com
- **Repositório:** https://github.com/studdw/nuva-api

---

## Arquitetura (MVC + DAO + BO)

```
Front React ──HTTP/JSON──► Resource (REST) ──► BO (regras) ──► DAO (SQL) ──► ConnectionFactory ──► Oracle
                                  ▲                                                        
                       CorsFilter │ GlobalExceptionHandler (erros → JSON)
```

```
src/main/java/br/com/fiap/api
├── ApiApplication.java
├── config/      ConnectionFactory, CorsFilter
├── model/       Liga, Usuario, Missao, Validacao, StatusValidacao, Conversao, Contato
├── dto/         Carteira
├── dao/         LigaDao, UsuarioDao, MissaoDao, ValidacaoDao, ConversaoDao, ContatoDao
├── bo/          LigaBo, UsuarioBo, MissaoBo, ValidacaoBo, ConversaoBo, ContatoBo, Validador
├── resource/    LigaResource, UsuarioResource, MissaoResource, ValidacaoResource,
│                ConversaoResource, ContatoResource, StatusResource
└── exception/   EntidadeNaoEncontradaException, RegraNegocioException,
                 ErroResposta, GlobalExceptionHandler
scripts.sql      criação das tabelas, sequences e carga inicial
Dockerfile       build/deploy no Render
```

## Modelo de dados (MER)

| Tabela | Chave | Relacionamentos |
|---|---|---|
| `tb_nuva_liga` | `cd_liga` | 1 liga : N usuários |
| `tb_nuva_usuario` | `cd_usuario` | FK `cd_liga` |
| `tb_nuva_missao` | `cd_missao` | 1 missão : N validações |
| `tb_nuva_validacao` | `cd_validacao` | FK `cd_usuario`, `cd_missao` |
| `tb_nuva_conversao` | `cd_conversao` | FK `cd_usuario` |
| `tb_nuva_contato` | `cd_contato` | — |

## Regras de negócio (camada BO)

| BO | Regra |
|---|---|
| `ValidacaoBo` | Somente MP4/MOV · 1 validação aprovada por missão/dia · máx. 3 tentativas/dia · sem rosto visível = `RECUSADO` · aprovado credita SoulCoins |
| `ConversaoBo` | Mínimo 100 SC · taxa fixa 100 SC = R$ 1,00 · débito atômico (sem saldo negativo) · cancelamento estorna os coins |
| `UsuarioBo` | Nome ≥ 3 · e-mail válido e único · saldo inicial 0 · troca de liga só se houver vaga |
| `LigaBo` | Máx. 200 participantes · não remove liga com participantes |
| `MissaoBo` | Critério de validação obrigatório · recompensa 10–1000 · dificuldade válida · missão desativada não pode ser reativada |
| `ContatoBo` | Nome ≥ 3 · e-mail válido · assunto da lista · mensagem 10–1000 caracteres |

## Endpoints

Base: `https://nuva-api.onrender.com`

| Recurso | Método | Endpoint | Descrição | Sucesso | Erros |
|---|---|---|---|---|---|
| Status | GET | `/` | Health check | 200 | — |
| Missões | GET | `/missoes` | Lista missões ativas (`?todas=true` inclui inativas) | 200 | — |
| Missões | GET | `/missoes/{id}` | Busca missão | 200 | 404 |
| Missões | POST | `/missoes` | Cadastra missão | 201 | 400 |
| Missões | PUT | `/missoes/{id}` | Atualiza / desativa missão | 200 | 400, 404 |
| Missões | DELETE | `/missoes/{id}` | Remove missão | 204 | 404 |
| Validações | GET | `/validacoes` | Lista validações | 200 | — |
| Validações | GET | `/validacoes/{id}` | Busca validação | 200 | 404 |
| Validações | POST | `/validacoes` | Envia vídeo para validação da IA | 201 | 400, 404 |
| Validações | PUT | `/validacoes/{id}` | Revisão manual do status | 200 | 400, 404 |
| Validações | DELETE | `/validacoes/{id}` | Remove validação | 204 | 404 |
| Usuários | GET | `/usuarios` | Lista usuários | 200 | — |
| Usuários | GET | `/usuarios/{id}` | Busca usuário | 200 | 404 |
| Usuários | GET | `/usuarios/{id}/carteira` | Saldo + histórico de conversões | 200 | 404 |
| Usuários | GET | `/usuarios/{id}/validacoes` | Histórico de validações | 200 | 404 |
| Usuários | POST | `/usuarios` | Cadastra usuário | 201 | 400, 409 |
| Usuários | PUT | `/usuarios/{id}` | Atualiza dados / troca de liga | 200 | 400, 404, 409 |
| Usuários | DELETE | `/usuarios/{id}` | Remove usuário | 204 | 404 |
| Ligas | GET | `/ligas` | Lista ligas com nº de participantes | 200 | — |
| Ligas | GET | `/ligas/{id}` | Busca liga | 200 | 404 |
| Ligas | GET | `/ligas/{id}/ranking` | Ranking semanal | 200 | 404 |
| Ligas | POST | `/ligas` | Cadastra liga | 201 | 400 |
| Ligas | PUT | `/ligas/{id}` | Atualiza liga | 200 | 400, 404 |
| Ligas | DELETE | `/ligas/{id}` | Remove liga vazia | 204 | 400, 404 |
| Conversões | GET | `/conversoes` | Lista conversões | 200 | — |
| Conversões | GET | `/conversoes/{id}` | Busca conversão | 200 | 404 |
| Conversões | POST | `/conversoes` | Converte SoulCoins em desconto | 201 | 400, 404 |
| Conversões | DELETE | `/conversoes/{id}` | Cancela e estorna coins | 204 | 404 |
| Contatos | GET | `/contatos` | Lista mensagens | 200 | — |
| Contatos | GET | `/contatos/{id}` | Busca mensagem | 200 | 404 |
| Contatos | POST | `/contatos` | Envia mensagem | 201 | 400 |
| Contatos | PUT | `/contatos/{id}` | Atualiza mensagem | 200 | 400, 404 |
| Contatos | DELETE | `/contatos/{id}` | Remove mensagem | 204 | 404 |

> Conversão não tem `PUT` de propósito: é um registro financeiro imutável; a correção é feita cancelando (DELETE com estorno) e convertendo de novo.

### Exemplos

`POST /validacoes`
```json
{ "usuarioId": 1, "missaoId": 1, "nomeArquivo": "reciclagem.mp4", "rostoVisivel": true }
```
Resposta `201`
```json
{ "id": 7, "usuarioId": 1, "missaoId": 1, "nomeArquivo": "reciclagem.mp4", "rostoVisivel": true,
  "status": "VALIDADO", "mensagem": "Missão validada! +120 SoulCoins creditados.", "dataEnvio": "2026-10-01T15:20:11" }
```

`POST /conversoes` → `{ "usuarioId": 1, "coins": 300 }`

Erro padrão (`GlobalExceptionHandler`)
```json
{ "status": 400, "erro": "Bad Request", "mensagem": "A conversão mínima é de 100 SoulCoins.",
  "caminho": "/conversoes", "dataHora": "2026-10-01T15:21:02" }
```

## Como executar

1. Rode o `scripts.sql` no Oracle (SQL Developer).
2. Configure as variáveis (ou edite `application.properties`):
   ```
   DB_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
   DB_USER=rmXXXXXX
   DB_PASSWORD=sua_senha
   CORS_ORIGINS=http://localhost:5173,https://nuva-guardioes-da-luz.vercel.app
   ```
3. `./mvnw spring-boot:run` (ou Run no IntelliJ em `ApiApplication`). API em `http://localhost:8080`.

## Deploy no Render

1. Suba este projeto para um repositório no GitHub.
2. Render → **New → Web Service** → conecte o repositório.
3. **Runtime: Docker** (o `Dockerfile` já está pronto).
4. Em **Environment**, cadastre `DB_URL`, `DB_USER`, `DB_PASSWORD` e `CORS_ORIGINS`.
5. Deploy. Teste em `https://SEU-SERVICO.onrender.com/missoes`.
6. Na Vercel (front), crie a variável `VITE_API_URL` com a URL do Render e faça redeploy.

> No plano gratuito o Render "dorme" após inatividade; a primeira requisição pode levar ~50s.

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Lucas Kaftan | 571302 | 1TDSPV |
| Matheus Iumatti | 571047 | 1TDSPV |
| Vinicius Silveira Espósito | 571844 | 1TDSPV |
| Joao Carlos | 568952 | 1TDSPV |
| Lucas Luque | 573347 | 1TDSPV |