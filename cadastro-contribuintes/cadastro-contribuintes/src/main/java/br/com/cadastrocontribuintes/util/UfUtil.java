package br.com.cadastrocontribuintes.util;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lista fechada das 27 unidades federativas brasileiras.
 */
public final class UfUtil {

    private static final Set<String> UFS_VALIDAS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
            "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    private UfUtil() {
    }

    public static boolean isValida(String uf) {
        return uf != null && UFS_VALIDAS.contains(uf.toUpperCase());
    }

    public static List<String> listarOrdenadas() {
        return List.copyOf(new TreeSet<>(UFS_VALIDAS));
    }
}
