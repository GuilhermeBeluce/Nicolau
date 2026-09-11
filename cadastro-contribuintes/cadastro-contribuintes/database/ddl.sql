-- ============================================================
-- Cadastro de Contribuintes - DDL
-- MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS cadastro_contribuintes
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE cadastro_contribuintes;

DROP TABLE IF EXISTS cadastro_contribuintes;

CREATE TABLE cadastro_contribuintes (
    doc_corpo                VARCHAR(9)      NOT NULL,
    doc_filial                CHAR(4)         NOT NULL DEFAULT '0000',
    doc_digito                CHAR(2)         NOT NULL,
    tipo_documento             ENUM('CPF','CNPJ') NOT NULL,
    quantidade_funcionarios    INT UNSIGNED    NOT NULL DEFAULT 0,
    faturamento_anual          DECIMAL(15,2) UNSIGNED NOT NULL DEFAULT 0.00,
    data_abertura              DATE            NOT NULL,
    razao_social_nome          VARCHAR(120)    NOT NULL,
    nome_fantasia              VARCHAR(80)     NULL,
    ddd_telefone               CHAR(2)         NOT NULL,
    numero_telefone            VARCHAR(9)      NOT NULL,
    email_corporativo          VARCHAR(100)    NOT NULL,
    cidade_sede                VARCHAR(60)     NOT NULL,
    estado_uf                  CHAR(2)         NOT NULL,
    PRIMARY KEY (doc_corpo, doc_filial, doc_digito)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- Indice de apoio para a pesquisa por nome/razao social (correspondencia parcial, ordenacao estavel)
CREATE INDEX idx_contribuintes_razao_social ON cadastro_contribuintes (razao_social_nome);
