package br.com.cadastrocontribuintes.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sinaliza que um ou mais campos do formulario nao passaram na validacao
 * centralizada. Mantem a ordem de insercao para permitir posicionar o foco
 * no primeiro campo invalido.
 */
public class ValidacaoException extends RuntimeException {

    private final Map<String, String> errosPorCampo = new LinkedHashMap<>();

    public ValidacaoException(Map<String, String> erros) {
        super("Falha de validação: " + erros.size() + " campo(s) inválido(s).");
        this.errosPorCampo.putAll(erros);
    }

    public Map<String, String> getErrosPorCampo() {
        return errosPorCampo;
    }

    public String getPrimeiroCampoInvalido() {
        return errosPorCampo.keySet().stream().findFirst().orElse(null);
    }
}
