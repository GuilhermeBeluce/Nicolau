package br.com.cadastrocontribuintes.bean;

import br.com.cadastrocontribuintes.formatter.FormatadorUtil;
import br.com.cadastrocontribuintes.model.Contribuinte;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.service.ContribuinteService;
import br.com.cadastrocontribuintes.util.DocumentoUtil;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Optional;

/**
 * Controla a tela de detalhamento, exclusivamente de leitura.
 */
@Named
@ViewScoped
public class ContribuinteDetalheBean implements Serializable {

    private static final String NAO_INFORMADO = "Não informado";
    private static final String NAO_APLICAVEL = "Não aplicável";

    @Inject
    private ContribuinteService service;

    private String tipoParam;
    private String documentoParam;

    private String origemDocumento;
    private String origemNome;
    private int origemPagina = 1;

    private Contribuinte contribuinte;
    private boolean registroNaoEncontrado;

    public void inicializar() {
        if (tipoParam == null || documentoParam == null) {
            registroNaoEncontrado = true;
            return;
        }

        TipoDocumento tipo = TipoDocumento.valueOf(tipoParam.toUpperCase());
        Optional<Contribuinte> encontrado = service.detalhar(tipo, documentoParam);

        if (encontrado.isEmpty()) {
            registroNaoEncontrado = true;
            return;
        }

        contribuinte = encontrado.get();
    }

    public String getDocumentoMascarado() {
        return DocumentoUtil.aplicarMascara(
                contribuinte.getTipoDocumento(), contribuinte.getDocumentoCompletoNormalizado());
    }

    public String getNomeFantasiaExibicao() {
        if (contribuinte.getTipoDocumento() == TipoDocumento.CPF) {
            return NAO_APLICAVEL;
        }
        return contribuinte.getNomeFantasia() == null ? NAO_INFORMADO : contribuinte.getNomeFantasia();
    }

    public String getTelefoneExibicao() {
        return FormatadorUtil.formatarTelefone(contribuinte.getDddTelefone(), contribuinte.getNumeroTelefone());
    }

    public String getCidadeUfExibicao() {
        return FormatadorUtil.formatarCidadeUf(contribuinte.getCidadeSede(), contribuinte.getEstadoUf());
    }

    public String getDataAberturaExibicao() {
        return FormatadorUtil.formatarData(contribuinte.getDataAbertura());
    }

    public String getQuantidadeFuncionariosExibicao() {
        if (contribuinte.getTipoDocumento() == TipoDocumento.CPF) {
            return NAO_APLICAVEL;
        }
        return String.valueOf(contribuinte.getQuantidadeFuncionarios());
    }

    public String getFaturamentoAnualExibicao() {
        return FormatadorUtil.formatarMoeda(contribuinte.getFaturamentoAnual());
    }

    public String getTipoDocumentoExibicao() {
        return contribuinte.getTipoDocumento().name();
    }

    public String getTipoParam() {
        return tipoParam;
    }

    public void setTipoParam(String tipoParam) {
        this.tipoParam = tipoParam;
    }

    public String getDocumentoParam() {
        return documentoParam;
    }

    public void setDocumentoParam(String documentoParam) {
        this.documentoParam = documentoParam;
    }

    public String getOrigemDocumento() {
        return origemDocumento;
    }

    public void setOrigemDocumento(String origemDocumento) {
        this.origemDocumento = origemDocumento;
    }

    public String getOrigemNome() {
        return origemNome;
    }

    public void setOrigemNome(String origemNome) {
        this.origemNome = origemNome;
    }

    public int getOrigemPagina() {
        return origemPagina;
    }

    public void setOrigemPagina(int origemPagina) {
        this.origemPagina = origemPagina;
    }

    public Contribuinte getContribuinte() {
        return contribuinte;
    }

    public boolean isRegistroNaoEncontrado() {
        return registroNaoEncontrado;
    }
}
