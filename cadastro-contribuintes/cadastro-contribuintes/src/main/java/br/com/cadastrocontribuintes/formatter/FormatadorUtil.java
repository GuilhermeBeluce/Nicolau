package br.com.cadastrocontribuintes.formatter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Centraliza toda a formatacao de exibicao (moeda, data, telefone, cidade/UF).
 * Nunca mistura formatacao visual com persistencia: os valores formatados
 * aqui jamais sao gravados no banco.
 */
public final class FormatadorUtil {

    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale LOCALE_BR = Locale.of("pt", "BR");

    private FormatadorUtil() {
    }

    public static String formatarMoeda(BigDecimal valor) {
        BigDecimal valorExibido = valor == null ? BigDecimal.ZERO : valor;
        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE_BR);
        return formato.format(valorExibido);
    }

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(FORMATO_DATA_BR);
    }

    /**
     * (XX) XXXX-XXXX para telefone fixo (8 digitos) e
     * (XX) XXXXX-XXXX para celular (9 digitos).
     */
    public static String formatarTelefone(String ddd, String numero) {
        if (ddd == null || numero == null) {
            return "";
        }
        if (numero.length() == 9) {
            return "(%s) %s-%s".formatted(ddd, numero.substring(0, 5), numero.substring(5));
        }
        return "(%s) %s-%s".formatted(ddd, numero.substring(0, 4), numero.substring(4));
    }

    public static String formatarCidadeUf(String cidade, String uf) {
        if (cidade == null || uf == null) {
            return "";
        }
        return cidade + " - " + uf;
    }
}
