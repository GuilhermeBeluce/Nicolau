package br.com.cadastrocontribuintes.dto;

import java.io.Serializable;

/**
 * Filtros de pesquisa da listagem, com paginacao. O tamanho de pagina e
 * sempre 10, conforme a especificacao.
 */
public class FiltroPesquisa implements Serializable {

    public static final int TAMANHO_PAGINA = 10;

    private String documento;
    private String nomeRazaoSocial;
    private int pagina = 1;

    public FiltroPesquisa() {
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = Math.max(pagina, 1);
    }

    public int getTamanhoPagina() {
        return TAMANHO_PAGINA;
    }
}
