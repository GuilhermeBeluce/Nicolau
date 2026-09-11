package br.com.cadastrocontribuintes.exception;

/**
 * Sinaliza uma regra de negocio violada durante uma operacao (por exemplo,
 * documento duplicado ou registro inexistente), identificada por um codigo
 * previsivel do catalogo de MensagemNegocio.
 */
public class OperacaoNegocioException extends RuntimeException {

    private final String codigo;

    public OperacaoNegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
