package br.com.cadastrocontribuintes.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultadoPaginadoTest {

    @Test
    void deveCalcularTotalDePaginasArredondandoParaCima() {
        ResultadoPaginado<String> resultado = new ResultadoPaginado<>(List.of("a"), 25, 1, 10);
        assertEquals(3, resultado.getTotalPaginas());
    }

    @Test
    void deveConsiderarUmaPaginaQuandoNaoHaRegistros() {
        ResultadoPaginado<String> resultado = new ResultadoPaginado<>(List.of(), 0, 1, 10);
        assertEquals(1, resultado.getTotalPaginas());
    }

    @Test
    void deveIdentificarPrimeiraPagina() {
        ResultadoPaginado<String> resultado = new ResultadoPaginado<>(List.of("a"), 30, 1, 10);
        assertTrue(resultado.isPrimeiraPagina());
        assertFalse(resultado.isUltimaPagina());
    }

    @Test
    void deveIdentificarUltimaPagina() {
        ResultadoPaginado<String> resultado = new ResultadoPaginado<>(List.of("a"), 30, 3, 10);
        assertTrue(resultado.isUltimaPagina());
        assertFalse(resultado.isPrimeiraPagina());
    }
}
