package br.com.cadastrocontribuintes.bean;

import br.com.cadastrocontribuintes.dto.ContribuinteFormulario;
import br.com.cadastrocontribuintes.dto.ResultadoOperacao;
import br.com.cadastrocontribuintes.exception.OperacaoNegocioException;
import br.com.cadastrocontribuintes.exception.PersistenciaException;
import br.com.cadastrocontribuintes.exception.ValidacaoException;
import br.com.cadastrocontribuintes.model.Contribuinte;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.service.ContribuinteService;
import br.com.cadastrocontribuintes.util.DocumentoDecomposto;
import br.com.cadastrocontribuintes.util.DocumentoUtil;
import br.com.cadastrocontribuintes.util.UfUtil;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controla o formulario unico de inclusao e alteracao de contribuinte.
 * O modo (inclusao ou alteracao) e definido pela presenca dos parametros
 * de navegacao tipo/documento, recebidos como view params.
 *
 * Na alteracao, a chave original e mantida internamente e nunca depende do
 * que o usuario possa reenviar no formulario.
 */
@Named
@ViewScoped
public class ContribuinteFormBean implements Serializable {

    @Inject
    private ContribuinteService service;

    private String tipoParam;
    private String documentoParam;

    // Preservacao de contexto da listagem (filtros e pagina) para o botao Cancelar/Voltar.
    private String origemDocumento;
    private String origemNome;
    private int origemPagina = 1;

    private ContribuinteFormulario formulario = new ContribuinteFormulario();
    private ContribuinteFormulario valoresOriginais;
    private DocumentoDecomposto chaveOriginal;
    private boolean modoAlteracao;
    private boolean registroNaoEncontrado;
    private boolean salvando;

    public void inicializar() {
        if (tipoParam == null || documentoParam == null || tipoParam.isBlank() || documentoParam.isBlank()) {
            modoAlteracao = false;
            formulario = new ContribuinteFormulario();
            return;
        }

        modoAlteracao = true;
        TipoDocumento tipo = TipoDocumento.valueOf(tipoParam.toUpperCase());
        Optional<Contribuinte> encontrado = service.detalhar(tipo, documentoParam);

        if (encontrado.isEmpty()) {
            registroNaoEncontrado = true;
            return;
        }

        Contribuinte contribuinte = encontrado.get();
        chaveOriginal = new DocumentoDecomposto(
                contribuinte.getDocCorpo(), contribuinte.getDocFilial(), contribuinte.getDocDigito());

        formulario = converterParaFormulario(contribuinte);
        valoresOriginais = converterParaFormulario(contribuinte);
    }

    /**
     * Ao trocar o tipo de documento antes de salvar, limpa documento, nome
     * fantasia e demais campos condicionais incompativeis.
     */
    public void aoAlterarTipoDocumento() {
        formulario.setDocumentoBruto(null);
        formulario.setNomeFantasia(null);
        formulario.setQuantidadeFuncionarios(null);
    }

    public String salvar() {
        salvando = true;
        try {
            ResultadoOperacao resultadoOperacao = modoAlteracao
                    ? service.alterar(chaveOriginal, formulario)
                    : service.incluir(formulario);

            if (resultadoOperacao.isSucesso()) {
                return prepararRedirecionamentoComMensagem(resultadoOperacao.mensagem());
            }

            adicionarMensagemGlobal(FacesMessage.SEVERITY_WARN, resultadoOperacao.mensagem());
            return null;
        } catch (ValidacaoException e) {
            tratarErrosDeValidacao(e);
            return null;
        } catch (PersistenciaException | OperacaoNegocioException e) {
            adicionarMensagemGlobal(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return null;
        } finally {
            salvando = false;
        }
    }

    /**
     * Restaura os campos editaveis aos valores originalmente carregados,
     * sem nova consulta ao banco e sem persistir nada.
     */
    public void restaurar() {
        if (valoresOriginais != null) {
            formulario = converterParaFormulario(valoresOriginais);
        }
    }

    public String cancelar() {
        return montarOutcomeListagem();
    }

    /**
     * Marca a mensagem de sucesso para sobreviver ao redirecionamento
     * (Flash scope) e retorna o outcome de navegacao para a listagem,
     * preservando pagina e filtros de origem.
     */
    private String prepararRedirecionamentoComMensagem(String mensagem) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        contexto.getExternalContext().getFlash().setKeepMessages(true);
        adicionarMensagemGlobal(FacesMessage.SEVERITY_INFO, mensagem);
        return montarOutcomeListagem();
    }

    private String montarOutcomeListagem() {
        return "/contribuintes/lista.xhtml?faces-redirect=true" + montarQueryStringSemInterrogacao();
    }

    private String montarQueryStringSemInterrogacao() {
        StringBuilder query = new StringBuilder();
        query.append("pagina=").append(origemPagina);
        if (origemDocumento != null && !origemDocumento.isBlank()) {
            query.append("&documento=").append(origemDocumento);
        }
        if (origemNome != null && !origemNome.isBlank()) {
            query.append("&nome=").append(origemNome);
        }
        return query.toString();
    }

    private void tratarErrosDeValidacao(ValidacaoException e) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        for (Map.Entry<String, String> erro : e.getErrosPorCampo().entrySet()) {
            contexto.addMessage("formularioContribuinte:" + erro.getKey(),
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, erro.getValue(), null));
        }
        String primeiroCampo = e.getPrimeiroCampoInvalido();
        if (primeiroCampo != null) {
            contexto.getExternalContext().getRequestMap().put("focoCampoInvalido", primeiroCampo);
        }
    }

    private void adicionarMensagemGlobal(FacesMessage.Severity severidade, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, texto, null));
    }

    private ContribuinteFormulario converterParaFormulario(Contribuinte contribuinte) {
        ContribuinteFormulario dados = new ContribuinteFormulario();
        dados.setTipoDocumento(contribuinte.getTipoDocumento());
        dados.setDocumentoBruto(DocumentoUtil.aplicarMascara(
                contribuinte.getTipoDocumento(), contribuinte.getDocumentoCompletoNormalizado()));
        dados.setRazaoSocialNome(contribuinte.getRazaoSocialNome());
        dados.setNomeFantasia(contribuinte.getNomeFantasia());
        dados.setDddTelefone(contribuinte.getDddTelefone());
        dados.setNumeroTelefone(contribuinte.getNumeroTelefone());
        dados.setEmailCorporativo(contribuinte.getEmailCorporativo());
        dados.setCidadeSede(contribuinte.getCidadeSede());
        dados.setEstadoUf(contribuinte.getEstadoUf());
        dados.setDataAbertura(contribuinte.getDataAbertura());
        dados.setQuantidadeFuncionarios(contribuinte.getQuantidadeFuncionarios());
        dados.setFaturamentoAnual(contribuinte.getFaturamentoAnual());
        return dados;
    }

    private ContribuinteFormulario converterParaFormulario(ContribuinteFormulario origem) {
        ContribuinteFormulario copia = new ContribuinteFormulario();
        copia.setTipoDocumento(origem.getTipoDocumento());
        copia.setDocumentoBruto(origem.getDocumentoBruto());
        copia.setRazaoSocialNome(origem.getRazaoSocialNome());
        copia.setNomeFantasia(origem.getNomeFantasia());
        copia.setDddTelefone(origem.getDddTelefone());
        copia.setNumeroTelefone(origem.getNumeroTelefone());
        copia.setEmailCorporativo(origem.getEmailCorporativo());
        copia.setCidadeSede(origem.getCidadeSede());
        copia.setEstadoUf(origem.getEstadoUf());
        copia.setDataAbertura(origem.getDataAbertura());
        copia.setQuantidadeFuncionarios(origem.getQuantidadeFuncionarios());
        copia.setFaturamentoAnual(origem.getFaturamentoAnual());
        return copia;
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

    public ContribuinteFormulario getFormulario() {
        return formulario;
    }

    public boolean isModoAlteracao() {
        return modoAlteracao;
    }

    public boolean isRegistroNaoEncontrado() {
        return registroNaoEncontrado;
    }

    public boolean isSalvando() {
        return salvando;
    }

    public TipoDocumento[] getTiposDocumento() {
        return TipoDocumento.values();
    }

    public List<String> getUfsDisponiveis() {
        return UfUtil.listarOrdenadas();
    }
}
