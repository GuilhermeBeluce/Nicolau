-- ============================================================
-- Cadastro de Contribuintes - Stored Procedures
-- MySQL 8.x
-- Toda a logica de negocio de persistencia vive aqui.
-- A aplicacao Java nunca executa INSERT/UPDATE/DELETE/SELECT de negocio diretamente.
-- ============================================================

USE cadastro_contribuintes;

DROP PROCEDURE IF EXISTS sp_contribuinte_incluir;
DROP PROCEDURE IF EXISTS sp_contribuinte_alterar;
DROP PROCEDURE IF EXISTS sp_contribuinte_detalhar;
DROP PROCEDURE IF EXISTS sp_contribuinte_pesquisar;
DROP PROCEDURE IF EXISTS sp_contribuinte_excluir;

DELIMITER $$

-- ------------------------------------------------------------
-- sp_contribuinte_incluir
-- Valida duplicidade de chave e insere o registro em uma unica transacao.
-- ------------------------------------------------------------
CREATE PROCEDURE sp_contribuinte_incluir (
    IN  p_doc_corpo                 VARCHAR(9),
    IN  p_doc_filial                CHAR(4),
    IN  p_doc_digito                CHAR(2),
    IN  p_tipo_documento            VARCHAR(4),
    IN  p_quantidade_funcionarios   INT UNSIGNED,
    IN  p_faturamento_anual         DECIMAL(15,2),
    IN  p_data_abertura             DATE,
    IN  p_razao_social_nome         VARCHAR(120),
    IN  p_nome_fantasia             VARCHAR(80),
    IN  p_ddd_telefone              CHAR(2),
    IN  p_numero_telefone           VARCHAR(9),
    IN  p_email_corporativo         VARCHAR(100),
    IN  p_cidade_sede               VARCHAR(60),
    IN  p_estado_uf                 CHAR(2),
    OUT p_codigo                    VARCHAR(10),
    OUT p_mensagem                  VARCHAR(200)
)
proc_incluir: BEGIN
    DECLARE v_existe INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_codigo = 'DB-001';
        SET p_mensagem = 'Não foi possível concluir a operação. Tente novamente.';
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_existe
    FROM cadastro_contribuintes
    WHERE doc_corpo = p_doc_corpo
      AND doc_filial = p_doc_filial
      AND doc_digito = p_doc_digito;

    IF v_existe > 0 THEN
        ROLLBACK;
        SET p_codigo = 'DOC-001';
        SET p_mensagem = 'Já existe um contribuinte cadastrado com este documento.';
        LEAVE proc_incluir;
    END IF;

    INSERT INTO cadastro_contribuintes (
        doc_corpo, doc_filial, doc_digito, tipo_documento,
        quantidade_funcionarios, faturamento_anual, data_abertura,
        razao_social_nome, nome_fantasia, ddd_telefone, numero_telefone,
        email_corporativo, cidade_sede, estado_uf
    ) VALUES (
        p_doc_corpo, p_doc_filial, p_doc_digito, p_tipo_documento,
        p_quantidade_funcionarios, p_faturamento_anual, p_data_abertura,
        p_razao_social_nome, p_nome_fantasia, p_ddd_telefone, p_numero_telefone,
        p_email_corporativo, p_cidade_sede, p_estado_uf
    );

    COMMIT;
    SET p_codigo = 'OK-001';
    SET p_mensagem = 'Contribuinte incluído com sucesso.';
END$$

-- ------------------------------------------------------------
-- sp_contribuinte_alterar
-- Atualiza apenas os campos editaveis. A chave (identidade) e imutavel
-- e serve unicamente para localizar o registro.
-- ------------------------------------------------------------
CREATE PROCEDURE sp_contribuinte_alterar (
    IN  p_doc_corpo                 VARCHAR(9),
    IN  p_doc_filial                CHAR(4),
    IN  p_doc_digito                CHAR(2),
    IN  p_quantidade_funcionarios   INT UNSIGNED,
    IN  p_faturamento_anual         DECIMAL(15,2),
    IN  p_data_abertura             DATE,
    IN  p_razao_social_nome         VARCHAR(120),
    IN  p_nome_fantasia             VARCHAR(80),
    IN  p_ddd_telefone              CHAR(2),
    IN  p_numero_telefone           VARCHAR(9),
    IN  p_email_corporativo         VARCHAR(100),
    IN  p_cidade_sede               VARCHAR(60),
    IN  p_estado_uf                 CHAR(2),
    OUT p_codigo                    VARCHAR(10),
    OUT p_mensagem                  VARCHAR(200),
    OUT p_linhas_afetadas           INT
)
proc_alterar: BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_codigo = 'DB-001';
        SET p_mensagem = 'Não foi possível concluir a operação. Tente novamente.';
        SET p_linhas_afetadas = 0;
    END;

    START TRANSACTION;

    UPDATE cadastro_contribuintes
       SET quantidade_funcionarios = p_quantidade_funcionarios,
           faturamento_anual       = p_faturamento_anual,
           data_abertura           = p_data_abertura,
           razao_social_nome       = p_razao_social_nome,
           nome_fantasia           = p_nome_fantasia,
           ddd_telefone            = p_ddd_telefone,
           numero_telefone         = p_numero_telefone,
           email_corporativo       = p_email_corporativo,
           cidade_sede             = p_cidade_sede,
           estado_uf               = p_estado_uf
     WHERE doc_corpo  = p_doc_corpo
       AND doc_filial = p_doc_filial
       AND doc_digito = p_doc_digito;

    SET p_linhas_afetadas = ROW_COUNT();

    IF p_linhas_afetadas = 0 THEN
        ROLLBACK;
        SET p_codigo = 'DOC-002';
        SET p_mensagem = 'O contribuinte não foi localizado. Atualize a listagem e tente novamente.';
    ELSE
        COMMIT;
        SET p_codigo = 'OK-002';
        SET p_mensagem = 'Contribuinte alterado com sucesso.';
    END IF;
END$$

-- ------------------------------------------------------------
-- sp_contribuinte_detalhar
-- Retorna zero ou um registro pela chave.
-- ------------------------------------------------------------
CREATE PROCEDURE sp_contribuinte_detalhar (
    IN p_doc_corpo  VARCHAR(9),
    IN p_doc_filial CHAR(4),
    IN p_doc_digito CHAR(2)
)
BEGIN
    SELECT doc_corpo, doc_filial, doc_digito, tipo_documento,
           quantidade_funcionarios, faturamento_anual, data_abertura,
           razao_social_nome, nome_fantasia, ddd_telefone, numero_telefone,
           email_corporativo, cidade_sede, estado_uf
      FROM cadastro_contribuintes
     WHERE doc_corpo  = p_doc_corpo
       AND doc_filial = p_doc_filial
       AND doc_digito = p_doc_digito;
END$$

-- ------------------------------------------------------------
-- sp_contribuinte_pesquisar
-- Filtros parametrizados (nunca concatenados), paginacao de ate 10
-- registros e ordenacao estavel por razao social e depois pelo documento.
-- ------------------------------------------------------------
CREATE PROCEDURE sp_contribuinte_pesquisar (
    IN  p_documento_filtro   VARCHAR(14),
    IN  p_nome_filtro        VARCHAR(120),
    IN  p_pagina             INT,
    IN  p_tamanho_pagina     INT,
    OUT p_total_registros    INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;
    DECLARE v_pagina INT DEFAULT 1;
    DECLARE v_tamanho_pagina INT DEFAULT 10;

    IF p_pagina IS NOT NULL AND p_pagina > 0 THEN
        SET v_pagina = p_pagina;
    END IF;

    IF p_tamanho_pagina IS NOT NULL AND p_tamanho_pagina > 0 AND p_tamanho_pagina <= 10 THEN
        SET v_tamanho_pagina = p_tamanho_pagina;
    END IF;

    SET v_offset = (v_pagina - 1) * v_tamanho_pagina;

    SELECT COUNT(*) INTO p_total_registros
      FROM cadastro_contribuintes
     WHERE (p_documento_filtro IS NULL OR p_documento_filtro = ''
            OR CONCAT(doc_corpo, doc_filial, doc_digito) LIKE CONCAT('%', p_documento_filtro, '%'))
       AND (p_nome_filtro IS NULL OR p_nome_filtro = ''
            OR razao_social_nome LIKE CONCAT('%', p_nome_filtro, '%'));

    SELECT doc_corpo, doc_filial, doc_digito, tipo_documento,
           quantidade_funcionarios, faturamento_anual, data_abertura,
           razao_social_nome, nome_fantasia, ddd_telefone, numero_telefone,
           email_corporativo, cidade_sede, estado_uf
      FROM cadastro_contribuintes
     WHERE (p_documento_filtro IS NULL OR p_documento_filtro = ''
            OR CONCAT(doc_corpo, doc_filial, doc_digito) LIKE CONCAT('%', p_documento_filtro, '%'))
       AND (p_nome_filtro IS NULL OR p_nome_filtro = ''
            OR razao_social_nome LIKE CONCAT('%', p_nome_filtro, '%'))
     ORDER BY razao_social_nome ASC, doc_corpo ASC, doc_filial ASC, doc_digito ASC
     LIMIT v_tamanho_pagina OFFSET v_offset;
END$$

-- ------------------------------------------------------------
-- sp_contribuinte_excluir
-- Exclusao fisica pela chave, respeitando restricoes de integridade.
-- ------------------------------------------------------------
CREATE PROCEDURE sp_contribuinte_excluir (
    IN  p_doc_corpo        VARCHAR(9),
    IN  p_doc_filial       CHAR(4),
    IN  p_doc_digito       CHAR(2),
    OUT p_codigo           VARCHAR(10),
    OUT p_mensagem         VARCHAR(200),
    OUT p_linhas_afetadas  INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLSTATE '23000'
    BEGIN
        ROLLBACK;
        SET p_codigo = 'DB-001';
        SET p_mensagem = 'Não foi possível concluir a operação. Tente novamente.';
        SET p_linhas_afetadas = 0;
    END;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_codigo = 'DB-001';
        SET p_mensagem = 'Não foi possível concluir a operação. Tente novamente.';
        SET p_linhas_afetadas = 0;
    END;

    START TRANSACTION;

    DELETE FROM cadastro_contribuintes
     WHERE doc_corpo  = p_doc_corpo
       AND doc_filial = p_doc_filial
       AND doc_digito = p_doc_digito;

    SET p_linhas_afetadas = ROW_COUNT();

    IF p_linhas_afetadas = 0 THEN
        ROLLBACK;
        SET p_codigo = 'DOC-002';
        SET p_mensagem = 'O contribuinte não foi localizado. Atualize a listagem e tente novamente.';
    ELSE
        COMMIT;
        SET p_codigo = 'OK-003';
        SET p_mensagem = 'Contribuinte excluído com sucesso.';
    END IF;
END$$

DELIMITER ;
