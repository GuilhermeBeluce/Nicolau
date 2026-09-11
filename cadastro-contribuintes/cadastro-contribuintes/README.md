# Cadastro de Contribuintes

Aplicação web monolítica em **Java 21 + Jakarta Faces (JSF) + CDI + MySQL 8**,
para inclusão, alteração, consulta, pesquisa e exclusão de contribuintes
pessoa física (CPF) e pessoa jurídica (CNPJ), conforme a
*Especificação Funcional — Cadastro de Contribuintes, versão 2.0*.

Todo o acesso a dados é feito **exclusivamente por Stored Procedures** via
`CallableStatement`. A aplicação Java nunca executa `INSERT`, `UPDATE`,
`DELETE` ou `SELECT` de negócio diretamente.

---

## 1. Objetivo

Centralizar o cadastro de contribuintes (CPF/CNPJ) em uma interface web
única, com listagem paginada, filtros de pesquisa, inclusão, alteração,
detalhamento somente leitura e exclusão física, sem autenticação (fora do
escopo desta versão).

## 2. Funcionalidades

- Listagem paginada (10 registros por página), ordenada por
  Nome/Razão social e, em empate, pelo documento.
- Pesquisa por Documento e por Nome/Razão social, com filtros preservados
  entre navegações (listagem ↔ detalhe ↔ alteração).
- Inclusão de contribuinte (CPF ou CNPJ), com campos condicionais
  habilitados conforme o tipo escolhido.
- Alteração de contribuinte existente, com Tipo de documento e Documento
  bloqueados (somente leitura) e botão **Restaurar** que repõe os valores
  originalmente carregados sem nova consulta ao banco.
- Detalhamento exclusivamente de leitura, com máscaras de exibição
  (documento, telefone, moeda, data).
- Exclusão física, com tela de confirmação mostrando Documento e
  Nome/Razão social.
- Mensagens de sucesso, atenção e erro padronizadas (catálogo de códigos
  `VAL-*`, `DOC-*`, `DB-*`, `OK-*`).

Fora do escopo (conforme a especificação): autenticação/login, cálculo de
dígito verificador de CPF/CNPJ, exclusão lógica e trilha de auditoria.

## 3. Arquitetura

Aplicação monolítica, único módulo Maven, empacotada como um único WAR.

```
Navegador
    |
    | HTTP
    v
Aplicação JSF (WAR único)
    |
    +-- XHTML / Facelets (apresentação)
    |
    +-- Managed Beans (@Named, CDI)  -> estado de tela, navegação
    |
    +-- Service (@ApplicationScoped) -> normalização e regras de negócio
    |
    +-- Repository (@ApplicationScoped) -> único ponto de acesso ao banco
    |
    +-- JDBC + CallableStatement
    |
    v
Stored Procedures
    |
    v
MySQL 8
```

### 3.1 Responsabilidade das camadas

| Camada | Responsabilidade | Não faz |
|---|---|---|
| XHTML | Apresentação, entrada de dados, mensagens | SQL, conexão, regra de negócio |
| Managed Bean (`bean`) | Estado da tela, navegação, paginação/filtros, foco no erro | JDBC, SQL, regra de negócio extensa |
| Service (`service`) | Normalização, validação, orquestração de inclusão/alteração/pesquisa/exclusão | JSF (`FacesContext`), SQL |
| Repository (`repository`) | `CallableStatement`, parâmetros, leitura de `ResultSet`, mapeamento | Formatação visual, `FacesMessage`, moeda/CPF para exibição |
| Validation (`validation`) | Regras de validação centralizadas e reutilizáveis | — |
| Formatter (`formatter`) | Máscaras e formatação de exibição (moeda, data, telefone) | Persistência |
| Util (`util`) | Normalização/decomposição/recomposição de documento, listas de DDD/UF | — |
| Exception (`exception`) | Catálogo de mensagens e exceções de negócio/técnicas | — |
| Config (`config`) | Fábrica de conexão JDBC via variáveis de ambiente | — |

## 4. Estrutura de pastas

```
cadastro-contribuintes/
    pom.xml
    README.md
    .gitignore
    db.properties.example
    database/
        ddl.sql
        procedures.sql
        seed.sql
    src/
        main/
            java/br/com/cadastrocontribuintes/
                bean/          Managed Beans (@Named, @ViewScoped)
                service/       Regras de negócio
                repository/    Acesso a dados via Stored Procedures
                model/         Contribuinte, TipoDocumento
                dto/           Formulário, filtro, resultado paginado/operação
                validation/    ContribuinteValidator
                exception/     Exceções e catálogo de mensagens
                formatter/     Formatação de exibição
                config/        ConexaoFactory
                util/          DocumentoUtil, DddUtil, UfUtil
            resources/
                logback.xml
            webapp/
                index.xhtml
                erro.xhtml
                contribuintes/
                    lista.xhtml
                    formulario.xhtml
                    detalhe.xhtml
                resources/css/estilo.css
                WEB-INF/
                    web.xml
                    beans.xml
                    faces-config.xml
                    templates/principal.xhtml
        test/
            java/br/com/cadastrocontribuintes/
                util/, formatter/, validation/, dto/
```

## 5. Tecnologias e versões exatas

| Item | Versão | Justificativa resumida |
|---|---|---|
| Java | 21 (LTS) | Exigido pelo ambiente. |
| Apache Tomcat | 10.1.x | Primeira linha do Tomcat compatível com Jakarta EE 10 (namespaces `jakarta.*`). |
| Jakarta Faces (API) | 4.0.1 | Versão da especificação correspondente ao Jakarta EE 10. |
| Implementação JSF | Mojarra 4.0.7 (`org.glassfish:jakarta.faces`) | Implementação de referência, estável, compatível com Jakarta Faces 4.0. |
| Jakarta EL | 5.0.1 (`org.glassfish:jakarta.el`) | Exigida pelo Mojarra fora de um servidor Jakarta EE completo. |
| CDI (API) | jakarta.enterprise.cdi-api 4.0.1 | Especificação correspondente ao Jakarta EE 10. |
| Implementação CDI | Weld Servlet 5.1.2.Final (`weld-servlet-shaded`) | Implementação de referência do CDI para ambiente Servlet puro (Tomcat não traz CDI embutido). |
| Jakarta Annotations | 2.1.1 | Dependência transitiva exigida por Weld/Mojarra fora de um servidor completo. |
| Servlet API | jakarta.servlet-api 6.0.0 (`provided`) | Compatível com Tomcat 10.1.x. |
| MySQL | 8.x | Exigido pelo ambiente. |
| Driver JDBC | mysql-connector-j 8.4.0 | Driver oficial e atual para MySQL 8, com suporte a `jakarta.*`. |
| Maven | 3.9.x | Build do projeto. |
| Logging | SLF4J 2.0.13 + Logback 1.5.6 | Combinação estável e amplamente usada; evita `System.out.println`. |
| JUnit | 5.10.2 | Testes unitários. |
| Mockito | 5.11.0 | Testes com dublês (não utilizado nos testes atuais, disponível para evolução). |

## 6. Pré-requisitos

- JDK 21 instalado e configurado.
- Apache Tomcat 10.1.x.
- MySQL 8.x em execução.
- Maven 3.9.x (ou o Maven embutido do IntelliJ IDEA).
- IntelliJ IDEA (Community ou Ultimate).

## 7. Configuração do banco de dados

1. Execute, nesta ordem, os scripts em `database/`:
   ```
   mysql -u root -p < database/ddl.sql
   mysql -u root -p < database/procedures.sql
   mysql -u root -p < database/seed.sql
   ```
2. `ddl.sql` cria o schema `cadastro_contribuintes` e a tabela.
3. `procedures.sql` cria as 5 Stored Procedures (remove e recria, pode ser
   executado quantas vezes for necessário).
4. `seed.sql` insere 15 registros fictícios (6 CPF + 6 CNPJ + 3 adicionais),
   suficientes para testar a paginação (mais de 10 registros).

## 8. Variáveis de ambiente

A aplicação **nunca** grava URL, usuário ou senha do banco no código. Ela lê
três variáveis de ambiente obrigatórias, lidas por `ConexaoFactory`:

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/cadastro_contribuintes?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `sua_senha` |

Veja `db.properties.example` (arquivo de referência, sem segredos — **não**
é lido pela aplicação, serve apenas de modelo).

### Configurando no IntelliJ IDEA

1. `Run` → `Edit Configurations...`.
2. Selecione a configuração do Tomcat (ver seção 10).
3. Aba **Startup/Connection** → **Environment variables** → adicione
   `DB_URL`, `DB_USER`, `DB_PASSWORD` com os valores do seu MySQL local.

## 9. Importando o projeto no IntelliJ IDEA

1. Instale e selecione o **JDK 21** em
   `File` → `Project Structure` → `Project SDK`.
2. `File` → `Open...` e selecione a pasta `cadastro-contribuintes`
   (o `pom.xml` na raiz é detectado automaticamente).
3. Aguarde o IntelliJ importar as dependências Maven (ícone de Maven no
   canto direito → `Reload All Maven Projects` se necessário).
4. Configure o Maven em `Settings` → `Build Tools` → `Maven` (pode usar o
   Maven embutido do IntelliJ).

## 10. Configurando o Apache Tomcat 10.1 no IntelliJ IDEA

1. Baixe e extraia o Apache Tomcat 10.1.x.
2. `Run` → `Edit Configurations...` → `+` → `Tomcat Server` → `Local`.
3. Em **Application server**, clique em `Configure...` e aponte para a
   pasta onde o Tomcat 10.1 foi extraído.
4. Aba **Deployment** → `+` → `Artifact...` → selecione
   `cadastro-contribuintes:war` (ou `war exploded`).
5. Defina o **Application context** (context path) como:
   ```
   /cadastro-contribuintes
   ```
6. Aba **Startup/Connection** → **Environment variables**: configure
   `DB_URL`, `DB_USER`, `DB_PASSWORD` (seção 8).
7. Aplique e salve a configuração.

## 11. Executando

1. Certifique-se de que o MySQL está em execução e os scripts da seção 7
   foram executados.
2. Rode a configuração do Tomcat criada na seção 10 (`Run` ou `Debug`).
3. Acesse:
   ```
   http://localhost:8080/cadastro-contribuintes/
   ```

## 12. Build via linha de comando

```
mvn clean package
```

Gera `target/cadastro-contribuintes.war`.

## 13. Executando os testes

Pela IDE: botão direito na pasta `src/test/java` → `Run 'All Tests'`.

Pelo terminal:
```
mvn test
```

Os testes cobrem: normalização/decomposição/recomposição/máscara de
CPF e CNPJ, validação de todos os campos do formulário (nome, nome
fantasia, e-mail, DDD, telefone, cidade, UF, data de abertura, quantidade
de funcionários, faturamento anual), formatação de exibição (moeda, data,
telefone, cidade/UF) e cálculo de paginação.

## 14. Decisões técnicas registradas

- **Namespaces Jakarta puros (`jakarta.*`)**, sem nenhum `javax.*`, por
  exigência do ambiente (Tomcat 10.1 / Jakarta EE 10).
- **CDI via Weld Servlet**, já que o Tomcat não é um servidor Jakarta EE
  completo e não traz CDI embutido; `beans.xml` com
  `bean-discovery-mode="annotated"` ativa a descoberta dos beans anotados.
- **Managed Beans com `@Named` + `jakarta.faces.view.ViewScoped`** (não o
  legado `jakarta.faces.bean.ViewScoped`), por ser a forma correta de
  integrar CDI com JSF.
- **Conexão via `DriverManager` + variáveis de ambiente**, em vez de
  `DataSource`/JNDI, por ser a alternativa mais simples e direta para um
  primeiro projeto, evitando configuração adicional no `context.xml` do
  Tomcat. Pode ser evoluído para JNDI/pool de conexões futuramente.
- **Chave composta (`doc_corpo`, `doc_filial`, `doc_digito`)** mantida
  exatamente como definida na especificação v2.0, sem substituição por
  `id` artificial.
- **Sem cálculo de dígito verificador de CPF/CNPJ**, conforme
  explicitamente fora do escopo da especificação.
- **Faturamento anual**: campo de formulário usa `f:convertNumber` com
  `locale="en"` (separador decimal `.`) para evitar ambiguidade de parsing
  independente do locale do servidor; a exibição em telas de leitura usa
  `FormatadorUtil` com formatação monetária brasileira (`R$ #.##0,00`).
- **Data de abertura**: campo HTML nativo `type="date"` (ISO `yyyy-MM-dd`)
  convertido com `f:convertDateTime type="localDate"`, mapeando
  diretamente para `java.time.LocalDate`.
- **Confirmação de exclusão**: painel de confirmação próprio (HTML/CSS,
  acessível, sem depender de `window.confirm()`), seguindo o padrão visual
  da aplicação e evitando dependência de diálogos nativos do navegador.
- **Botão Salvar desabilitado durante o processamento**: pequeno trecho de
  JavaScript inline (`setTimeout` de 0ms) desabilita o botão após o clique
  disparar a submissão do formulário, evitando envio duplicado sem
  depender de nenhuma biblioteca externa.
- **Preservação de filtros/página entre telas**: como cada tela
  (listagem, formulário, detalhe) é uma view JSF independente, o contexto
  de origem (página atual, filtro de documento, filtro de nome) é
  transportado via parâmetros de URL (`f:param`/`f:viewParam`) e
  devolvido nos links de "Voltar"/"Cancelar" e no redirecionamento após
  salvar/alterar.
- **Logging**: SLF4J + Logback em vez de `java.util.logging`, por ser a
  combinação mais usada no mercado e mais simples de configurar
  (`logback.xml`), evitando qualquer `System.out.println`.

## 15. Limitações reais do ambiente de geração deste projeto

Este projeto foi criado em um ambiente de sandbox **sem acesso ao Maven
Central** (apenas alguns domínios específicos são permitidos: GitHub,
PyPI, npm, crates.io, repositórios Ubuntu, entre outros — **não** inclui
`repo1.maven.org`/`repo.maven.apache.org`) e **sem o compilador Java
(`javac`)** instalado (apenas o `java` de execução estava disponível).

Por isso:

- **Não foi possível executar `mvn clean package`** (as dependências
  declaradas no `pom.xml` não puderam ser baixadas).
- **Não foi possível executar `mvn test`** pelo mesmo motivo.
- **Não foi possível iniciar o Tomcat nem conectar a um MySQL real**,
  pois nenhum dos dois estava disponível no ambiente de geração.
- O que **foi feito**: revisão estática completa de todos os arquivos
  Java, XHTML e SQL; conferência manual de assinaturas dos
  `CallableStatement` contra os parâmetros das Stored Procedures;
  auditoria textual por padrões proibidos (seção 17).

**Para validar o projeto**, execute localmente, na ordem:

```
mvn -version
mvn clean compile
mvn test
mvn clean package
```

E siga as seções 7 a 11 deste README para configurar o banco, o Tomcat e
executar a aplicação de fato.

## 16. Solução de problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| `ClassNotFoundException: jakarta.faces.webapp.FacesServlet` | Mojarra não incluído no WAR | Confirme que a dependência `org.glassfish:jakarta.faces` está no `pom.xml` (escopo padrão, não `provided`). |
| CDI não injeta (`@Inject` nulo) | `beans.xml` ausente ou Weld não no classpath | Confirme `WEB-INF/beans.xml` e a dependência `weld-servlet-shaded`. |
| Erro de conexão ao iniciar uma operação | Variáveis de ambiente não configuradas | Configure `DB_URL`, `DB_USER`, `DB_PASSWORD` na configuração de execução do Tomcat (seção 8/10). |
| `PROCEDURE ... does not exist` | Scripts SQL não executados ou executados fora de ordem | Rode `ddl.sql`, depois `procedures.sql`, depois `seed.sql`. |
| Página em branco ou 404 em `.xhtml` | Mapeamento do `FacesServlet` incorreto | Confirme `web.xml` (`url-pattern` `*.xhtml`) e o contexto configurado no Tomcat. |
| Acentuação incorreta na tela | Charset incorreto na conexão | Confirme `characterEncoding=UTF-8` na `DB_URL` e `utf8mb4` no schema. |

## 17. Auditoria textual — padrões verificados

Busca realizada manualmente em todo o código-fonte (`src/main/java` e
`src/main/webapp`) pelos seguintes padrões:

- `javax.` → nenhuma ocorrência (todo o projeto usa `jakarta.*`).
- `TODO` / `FIXME` → nenhuma ocorrência.
- `System.out` → nenhuma ocorrência (uso de SLF4J).
- `INSERT` / `UPDATE` / `DELETE` / `SELECT` fora de `database/*.sql` →
  nenhuma ocorrência em código Java (todas as instruções SQL estão
  exclusivamente nos scripts SQL e dentro das Stored Procedures).
- `jdbc:` fixo no código → nenhuma ocorrência (lido de `DB_URL`).
- `password` / `senha` com valor literal → nenhuma ocorrência (lidos de
  variável de ambiente).
- `catch` vazio → nenhuma ocorrência (toda `SQLException` é logada e
  convertida em `PersistenciaException`/`ConfiguracaoException`).

## 18. Critérios de aceite

| ID | Critério | Implementação | Teste | Resultado |
|---|---|---|---|---|
| CA-01 | CPF: 9 dígitos em `doc_corpo`, `0000` em `doc_filial`, 2 em `doc_digito` | `DocumentoUtil.decompor` | `DocumentoUtilTest.deveDecomporCpfComFilialFixaZerada` | Verificado estaticamente* |
| CA-02 | CNPJ: 8+4+2 dígitos | `DocumentoUtil.decompor` | `DocumentoUtilTest.deveDecomporCnpjEmCorpoFilialEDigito` | Verificado estaticamente* |
| CA-02A | CPF 11 dígitos/máscara `999.999.999-99`; CNPJ 14 dígitos/máscara `99.999.999/9999-99` | `DocumentoUtil.aplicarMascara` + `ContribuinteValidator` | `DocumentoUtilTest` (máscaras) | Verificado estaticamente* |
| CA-03 | Impede segundo cadastro com a mesma chave | `sp_contribuinte_incluir` (verifica existência antes de inserir, retorna `DOC-001`) | Requer integração com MySQL | Pendente de execução real |
| CA-04 | Tipo e documento imutáveis após inclusão | `formulario.xhtml` (campos desabilitados) + `ContribuinteFormBean` (chave original nunca vem do formulário) | Revisão de código | Verificado estaticamente* |
| CA-05 | Campos condicionais de CPF/CNPJ | `ContribuinteService.montarContribuinte` + `ContribuinteValidator` | `ContribuinteValidatorTest` | Verificado estaticamente* |
| CA-06 | Nenhuma operação de negócio acessa tabela fora de Stored Procedures | `ContribuinteRepository` (somente `CallableStatement`) | Auditoria textual (seção 17) | Verificado estaticamente* |
| CA-07 | Pesquisa parametrizada, filtros preservados, até 10 registros/página | `sp_contribuinte_pesquisar` + `ContribuinteListagemBean` | `ResultadoPaginadoTest` | Verificado estaticamente* |
| CA-08 | Detalhamento sem edição, com máscaras corretas | `detalhe.xhtml` (somente `<dd>`, sem inputs) + `ContribuinteDetalheBean` | `FormatadorUtilTest` | Verificado estaticamente* |
| CA-09 | Exclusão exige confirmação; trata inexistente e restrição referencial | `lista.xhtml` (painel de confirmação) + `sp_contribuinte_excluir` (`DOC-002`, `SQLSTATE '23000'`) | Requer integração com MySQL | Pendente de execução real |
| CA-10 | Erros de validação não persistem dados e focam o primeiro campo inválido | `ContribuinteValidator` + `ValidacaoException` + `ContribuinteFormBean.tratarErrosDeValidacao` | `ContribuinteValidatorTest` | Verificado estaticamente* |

\* "Verificado estaticamente" significa: revisão manual do código-fonte e,
quando aplicável, teste unitário aprovado por leitura (não executado
neste ambiente — ver seção 15). **Nenhum critério foi marcado como
aprovado sem essa ressalva ser explícita.**

## 19. Itens fora do escopo (herdados da especificação)

- Autenticação, login e perfis de acesso.
- Validação matemática de dígito verificador de CPF/CNPJ.
- Exclusão lógica, histórico de alterações e trilha de auditoria.
- Integração automática com serviços externos de validação cadastral.

## 20. Pendências conhecidas

- Executar `mvn clean package`, `mvn test`, subir o Tomcat e o MySQL
  reais e validar ponta a ponta (não realizável no ambiente onde este
  projeto foi gerado — ver seção 15).
- CA-03 e CA-09 dependem de um MySQL real em execução para confirmação
  final (a lógica está implementada e revisada, mas não foi exercitada
  contra um banco de dados de fato).
