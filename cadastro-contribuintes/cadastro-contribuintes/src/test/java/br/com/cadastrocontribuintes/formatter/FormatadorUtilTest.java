package br.com.cadastrocontribuintes.formatter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatadorUtilTest {

    @Test
    void deveFormatarDataNoPadraoBrasileiro() {
        assertEquals("25/12/2020", FormatadorUtil.formatarData(LocalDate.of(2020, 12, 25)));
    }

    @Test
    void deveFormatarTelefoneFixoComOitoDigitos() {
        assertEquals("(43) 3333-4444", FormatadorUtil.formatarTelefone("43", "33334444"));
    }

    @Test
    void deveFormatarCelularComNoveDigitos() {
        assertEquals("(43) 99999-8888", FormatadorUtil.formatarTelefone("43", "999998888"));
    }

    @Test
    void deveFormatarCidadeEUf() {
        assertEquals("Londrina - PR", FormatadorUtil.formatarCidadeUf("Londrina", "PR"));
    }

    @Test
    void deveFormatarMoedaComDuasCasasDecimais() {
        String formatado = FormatadorUtil.formatarMoeda(new BigDecimal("1500000.50"));
        assertTrue(formatado.contains("1.500.000,50"));
    }

    @Test
    void deveFormatarMoedaZeroQuandoValorForNulo() {
        String formatado = FormatadorUtil.formatarMoeda(null);
        assertTrue(formatado.contains("0,00"));
    }
}
