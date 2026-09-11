package br.com.cadastrocontribuintes.exception;

/**
 * Catalogo centralizado dos codigos e mensagens de negocio da aplicacao.
 * Nenhuma mensagem tecnica (SQL, stack trace, nome de tabela ou procedure)
 * deve chegar ate o usuario: apenas os textos definidos aqui.
 */
public final class MensagemNegocio {

    public static final String VAL_001 = "VAL-001";
    public static final String VAL_002 = "VAL-002";
    public static final String DOC_001 = "DOC-001";
    public static final String DOC_002 = "DOC-002";
    public static final String DB_001 = "DB-001";
    public static final String OK_001 = "OK-001";
    public static final String OK_002 = "OK-002";
    public static final String OK_003 = "OK-003";

    public static final String MSG_DOC_001 = "Já existe um contribuinte cadastrado com este documento.";
    public static final String MSG_DOC_002 = "O contribuinte não foi localizado. Atualize a listagem e tente novamente.";
    public static final String MSG_DB_001 = "Não foi possível concluir a operação. Tente novamente.";
    public static final String MSG_OK_001 = "Contribuinte incluído com sucesso.";
    public static final String MSG_OK_002 = "Contribuinte alterado com sucesso.";
    public static final String MSG_OK_003 = "Contribuinte excluído com sucesso.";

    private MensagemNegocio() {
    }

    public static String campoObrigatorio(String nomeCampo) {
        return "Informe o campo " + nomeCampo + ".";
    }

    public static String formatoInvalido(String nomeCampo) {
        return "O campo " + nomeCampo + " está em formato inválido.";
    }
}
