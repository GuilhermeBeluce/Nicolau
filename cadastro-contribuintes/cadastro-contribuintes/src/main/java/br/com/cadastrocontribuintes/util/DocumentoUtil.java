package br.com.cadastrocontribuintes.util;

import br.com.cadastrocontribuintes.model.TipoDocumento;

/**
 * Ponto central de normalizacao, decomposicao, recomposicao e mascaramento
 * de CPF e CNPJ. Nao realiza calculo de digito verificador: essa validacao
 * esta expressamente fora do escopo da especificacao.
 */
public final class DocumentoUtil {

    private static final int TAMANHO_CORPO_CPF = 9;
    private static final int TAMANHO_CORPO_CNPJ = 8;
    private static final int TAMANHO_FILIAL = 4;
    private static final int TAMANHO_DIGITO = 2;
    private static final String FILIAL_CPF = "0000";

    private DocumentoUtil() {
    }

    /**
     * Remove pontos, barras, hifens e espacos, mantendo apenas os digitos.
     */
    public static String normalizar(String documento) {
        if (documento == null) {
            return "";
        }
        return documento.replaceAll("\\D", "");
    }

    /**
     * Verifica se o documento normalizado possui a quantidade de digitos
     * esperada para o tipo informado (11 para CPF, 14 para CNPJ).
     */
    public static boolean possuiQuantidadeDigitosValida(TipoDocumento tipo, String documentoNormalizado) {
        if (tipo == null || documentoNormalizado == null) {
            return false;
        }
        return documentoNormalizado.length() == tipo.getQuantidadeDigitos();
    }

    /**
     * Decompoe o documento normalizado na chave fracionada persistida no banco.
     * CPF: 9 (corpo) + 2 (digito), filial fixa em "0000".
     * CNPJ: 8 (corpo) + 4 (filial) + 2 (digito).
     *
     * @throws IllegalArgumentException se a quantidade de digitos nao for valida para o tipo
     */
    public static DocumentoDecomposto decompor(TipoDocumento tipo, String documentoNormalizado) {
        if (!possuiQuantidadeDigitosValida(tipo, documentoNormalizado)) {
            throw new IllegalArgumentException(
                    "Documento com quantidade de dígitos inválida para o tipo " + tipo);
        }

        if (tipo == TipoDocumento.CPF) {
            String corpo = documentoNormalizado.substring(0, TAMANHO_CORPO_CPF);
            String digito = documentoNormalizado.substring(TAMANHO_CORPO_CPF);
            return new DocumentoDecomposto(corpo, FILIAL_CPF, digito);
        }

        String corpo = documentoNormalizado.substring(0, TAMANHO_CORPO_CNPJ);
        String filial = documentoNormalizado.substring(TAMANHO_CORPO_CNPJ, TAMANHO_CORPO_CNPJ + TAMANHO_FILIAL);
        String digito = documentoNormalizado.substring(TAMANHO_CORPO_CNPJ + TAMANHO_FILIAL);
        return new DocumentoDecomposto(corpo, filial, digito);
    }

    /**
     * Recompoe o documento normalizado (somente digitos) a partir da chave fracionada.
     */
    public static String recompor(TipoDocumento tipo, DocumentoDecomposto chave) {
        if (tipo == TipoDocumento.CPF) {
            return chave.docCorpo() + chave.docDigito();
        }
        return chave.docCorpo() + chave.docFilial() + chave.docDigito();
    }

    /**
     * Aplica a mascara de exibicao correspondente ao tipo de documento.
     */
    public static String aplicarMascara(TipoDocumento tipo, String documentoNormalizado) {
        if (!possuiQuantidadeDigitosValida(tipo, documentoNormalizado)) {
            return documentoNormalizado;
        }

        if (tipo == TipoDocumento.CPF) {
            return documentoNormalizado.replaceFirst(
                    "(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }

        return documentoNormalizado.replaceFirst(
                "(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    /**
     * Aplica a mascara de exibicao diretamente a partir da chave fracionada.
     */
    public static String aplicarMascara(TipoDocumento tipo, DocumentoDecomposto chave) {
        return aplicarMascara(tipo, recompor(tipo, chave));
    }
}
