package br.com.cadastrocontribuintes.util;

/**
 * Chave fracionada do documento, tal como persistida no banco.
 */
public record DocumentoDecomposto(String docCorpo, String docFilial, String docDigito) {
}
