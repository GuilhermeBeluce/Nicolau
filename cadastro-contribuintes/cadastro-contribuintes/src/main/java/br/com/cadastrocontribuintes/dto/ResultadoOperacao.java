package br.com.cadastrocontribuintes.dto;

import java.io.Serializable;

/**
 * Resultado estruturado de uma operacao de escrita (incluir, alterar, excluir),
 * espelhando o retorno das Stored Procedures: codigo, mensagem e linhas afetadas.
 */
public record ResultadoOperacao(String codigo, String mensagem, int linhasAfetadas) implements Serializable {

    public boolean isSucesso() {
        return codigo != null && codigo.startsWith("OK-");
    }
}
