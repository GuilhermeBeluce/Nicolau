package br.com.cadastrocontribuintes.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Envelope de resultado de uma pesquisa paginada.
 */
public class ResultadoPaginado<T> implements Serializable {

    private final List<T> registros;
    private final long totalRegistros;
    private final int paginaAtual;
    private final int tamanhoPagina;

    public ResultadoPaginado(List<T> registros, long totalRegistros, int paginaAtual, int tamanhoPagina) {
        this.registros = registros;
        this.totalRegistros = totalRegistros;
        this.paginaAtual = paginaAtual;
        this.tamanhoPagina = tamanhoPagina;
    }

    public List<T> getRegistros() {
        return registros;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public int getTamanhoPagina() {
        return tamanhoPagina;
    }

    public int getTotalPaginas() {
        if (totalRegistros == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalRegistros / tamanhoPagina);
    }

    public boolean isPrimeiraPagina() {
        return paginaAtual <= 1;
    }

    public boolean isUltimaPagina() {
        return paginaAtual >= getTotalPaginas();
    }
}
