package br.com.cadastrocontribuintes.exception;

/**
 * Sinaliza que uma variavel de ambiente obrigatoria de configuracao
 * (DB_URL, DB_USER ou DB_PASSWORD) nao foi definida.
 */
public class ConfiguracaoException extends RuntimeException {

    public ConfiguracaoException(String mensagem) {
        super(mensagem);
    }
}
