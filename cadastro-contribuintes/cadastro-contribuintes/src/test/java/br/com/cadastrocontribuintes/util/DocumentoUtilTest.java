package br.com.cadastrocontribuintes.util;

import br.com.cadastrocontribuintes.model.TipoDocumento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentoUtilTest {

    @Test
    void deveNormalizarRemovendoPontuacaoDeCpf() {
        assertEquals("12345678900", DocumentoUtil.normalizar("123.456.789-00"));
    }

    @Test
    void deveNormalizarRemovendoPontuacaoDeCnpj() {
        assertEquals("12345678000199", DocumentoUtil.normalizar("12.345.678/0001-99"));
    }

    @Test
    void deveNormalizarDocumentoNulo() {
        assertEquals("", DocumentoUtil.normalizar(null));
    }

    @Test
    void deveValidarQuantidadeDeDigitosDoCpf() {
        assertTrue(DocumentoUtil.possuiQuantidadeDigitosValida(TipoDocumento.CPF, "12345678900"));
        assertFalse(DocumentoUtil.possuiQuantidadeDigitosValida(TipoDocumento.CPF, "1234567890"));
    }

    @Test
    void deveValidarQuantidadeDeDigitosDoCnpj() {
        assertTrue(DocumentoUtil.possuiQuantidadeDigitosValida(TipoDocumento.CNPJ, "12345678000199"));
        assertFalse(DocumentoUtil.possuiQuantidadeDigitosValida(TipoDocumento.CNPJ, "1234567800019"));
    }

    @Test
    void deveDecomporCpfComFilialFixaZerada() {
        DocumentoDecomposto chave = DocumentoUtil.decompor(TipoDocumento.CPF, "12345678900");
        assertEquals("123456789", chave.docCorpo());
        assertEquals("0000", chave.docFilial());
        assertEquals("00", chave.docDigito());
    }

    @Test
    void deveDecomporCnpjEmCorpoFilialEDigito() {
        DocumentoDecomposto chave = DocumentoUtil.decompor(TipoDocumento.CNPJ, "12345678000199");
        assertEquals("12345678", chave.docCorpo());
        assertEquals("0001", chave.docFilial());
        assertEquals("99", chave.docDigito());
    }

    @Test
    void deveLancarExcecaoAoDecomporDocumentoComTamanhoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> DocumentoUtil.decompor(TipoDocumento.CPF, "123"));
    }

    @Test
    void deveRecomporCpfIgnorandoFilial() {
        DocumentoDecomposto chave = new DocumentoDecomposto("123456789", "0000", "00");
        assertEquals("12345678900", DocumentoUtil.recompor(TipoDocumento.CPF, chave));
    }

    @Test
    void deveRecomporCnpjComFilial() {
        DocumentoDecomposto chave = new DocumentoDecomposto("12345678", "0001", "99");
        assertEquals("12345678000199", DocumentoUtil.recompor(TipoDocumento.CNPJ, chave));
    }

    @Test
    void deveAplicarMascaraDeCpf() {
        assertEquals("123.456.789-00", DocumentoUtil.aplicarMascara(TipoDocumento.CPF, "12345678900"));
    }

    @Test
    void deveAplicarMascaraDeCnpj() {
        assertEquals("12.345.678/0001-99", DocumentoUtil.aplicarMascara(TipoDocumento.CNPJ, "12345678000199"));
    }
}
