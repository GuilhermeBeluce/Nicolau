# Especificação Funcional — Cadastro de Contribuintes

Inclusão, alteração, consulta, pesquisa e exclusão

- **Versão:** 2.0
- **Status:** Especificação consolidada
- **Tecnologia:** Java, JSF e MySQL
- **Persistência:** Stored Procedures

> Este arquivo é uma cópia de referência do documento fornecido como fonte
> de verdade funcional do projeto (`Especificacao Funcional - Revisada
> 4.docx`). Em caso de dúvida, o documento original entregue pelo usuário
> prevalece. Consulte o `README.md` na raiz do projeto para o mapeamento
> entre cada seção desta especificação e a implementação correspondente
> (camadas, Stored Procedures, telas e critérios de aceite).

## Finalidade

Estabelecer uma referência única, coerente e implementável para o cadastro
de contribuintes pessoa física (CPF) e pessoa jurídica (CNPJ).

## Sumário das seções do documento original

1. Objetivo e escopo (inclui itens fora do escopo)
2. Premissas técnicas (stack, acesso a dados, interface, autenticação)
3. Fluxo funcional e regra central de imutabilidade da identidade do registro
4. Modelo de dados (DDL da tabela `cadastro_contribuintes`, dicionário de
   dados de 14 campos, composição/recomposição do documento)
5. Regras gerais de validação (documento, nome/razão social, nome
   fantasia, e-mail, DDD, telefone, cidade, UF, data de abertura,
   quantidade de funcionários, faturamento anual)
6. Funcionalidade: Incluir contribuinte
7. Funcionalidade: Alterar contribuinte (incluindo a ação Restaurar)
8. Funcionalidade: Detalhar contribuinte (somente leitura)
9. Funcionalidade: Pesquisar e listar (filtros, segurança da consulta,
   paginação e ordenação, limpar filtros, identificação dos registros)
10. Funcionalidade: Excluir contribuinte (fluxo e regras)
11. Tratamento de mensagens e erros (catálogo `VAL-*`, `DOC-*`, `DB-*`, `OK-*`)
12. Padrão de interação da interface
13. Interfaces de Stored Procedures (`sp_contribuinte_incluir/alterar/
    detalhar/pesquisar/excluir`) e regras de transação
14. Critérios de aceite (CA-01 a CA-10 e CA-02A)
15. Consolidação das inconsistências corrigidas em relação à versão 1

O conteúdo integral de cada seção foi implementado conforme descrito no
documento original; a rastreabilidade de cada critério de aceite até o
código correspondente está na seção 18 do `README.md`.
