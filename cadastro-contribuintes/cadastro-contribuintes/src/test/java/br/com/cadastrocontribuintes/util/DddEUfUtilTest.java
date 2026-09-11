package br.com.cadastrocontribuintes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DddEUfUtilTest {

    @Test
    void deveAceitarDddValido() {
        assertTrue(DddUtil.isValido("43"));
    }

    @Test
    void deveRejeitarDddInexistente() {
        assertFalse(DddUtil.isValido("00"));
        assertFalse(DddUtil.isValido(null));
    }

    @Test
    void deveAceitarUfValida() {
        assertTrue(UfUtil.isValida("PR"));
        assertTrue(UfUtil.isValida("pr"));
    }

    @Test
    void deveRejeitarUfInvalida() {
        assertFalse(UfUtil.isValida("ZZ"));
        assertFalse(UfUtil.isValida(null));
    }

    @Test
    void deveListarAsVinteSeteUnidadesFederativas() {
        assertEquals(27, UfUtil.listarOrdenadas().size());
    }
}
