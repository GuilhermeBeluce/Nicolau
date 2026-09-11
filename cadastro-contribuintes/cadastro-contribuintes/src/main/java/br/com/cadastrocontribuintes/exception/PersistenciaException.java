package br.com.cadastrocontribuintes.exception;

/**
 * Encapsula qualquer falha tecnica de acesso a dados (SQLException, falha de
 * conexao, etc.), garantindo que apenas uma mensagem generica de negocio
 * (DB-001) chegue ate a interface, nunca detalhes tecnicos do banco.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
