package br.com.cadastrocontribuintes.bean;

import br.com.cadastrocontribuintes.dto.FiltroPesquisa;
import br.com.cadastrocontribuintes.dto.ResultadoOperacao;
import br.com.cadastrocontribuintes.dto.ResultadoPaginado;
import br.com.cadastrocontribuintes.exception.PersistenciaException;
import br.com.cadastrocontribuintes.model.Contribuinte;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.service.ContribuinteService;
import br.com.cadastrocontribuintes.util.DocumentoDecomposto;
import br.com.cadastrocontribuintes.util.DocumentoUtil;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Controla a tela de listagem: pesquisa, filtros, paginacao e exclusao.
 * Nao acessa o banco diretamente; delega toda a regra ao Service.
 */
@Named
@ViewScoped
public class ContribuinteListagemBean implements Serializable {

    @Inject
    private ContribuinteService service;

    private FiltroPesquisa filtro = new FiltroPesquisa();
    private ResultadoPaginado<Contribuinte> resultado;
    private Contribuinte contribuinteSelecionadoParaExclusao;

    public void inicializar() {
        pesquisar();
    }

    public void pesquisar() {
        filtro.setPagina(1);
        executarPesquisa();
    }

    public void irParaPagina(int pagina) {
        filtro.setPagina(pagina);
        executarPesquisa();
    }

    public void paginaAnterior() {
        if (!resultado.isPrimeiraPagina()) {
            irParaPagina(resultado.getPaginaAtual() - 1);
        }
    }

    public void proximaPagina() {
        if (!resultado.isUltimaPagina()) {
            irParaPagina(resultado.getPaginaAtual() + 1);
        }
    }

    public void limparFiltros() {
        filtro = new FiltroPesquisa();
        executarPesquisa();
    }

    public void prepararExclusao(Contribuinte contribuinte) {
        this.contribuinteSelecionadoParaExclusao = contribuinte;
    }

    public void confirmarExclusao() {
        if (contribuinteSelecionadoParaExclusao == null) {
            return;
        }
        DocumentoDecomposto chave = new DocumentoDecomposto(
                contribuinteSelecionadoParaExclusao.getDocCorpo(),
                contribuinteSelecionadoParaExclusao.getDocFilial(),
                contribuinteSelecionadoParaExclusao.getDocDigito());

        try {
            ResultadoOperacao resultadoOperacao = service.excluir(chave);
            adicionarMensagem(resultadoOperacao.isSucesso()
                    ? FacesMessage.SEVERITY_INFO
                    : FacesMessage.SEVERITY_WARN, resultadoOperacao.mensagem());
        } catch (PersistenciaException e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } finally {
            contribuinteSelecionadoParaExclusao = null;
            executarPesquisa();
        }
    }

    public void cancelarExclusao() {
        contribuinteSelecionadoParaExclusao = null;
    }

    public String mascararDocumento(Contribuinte contribuinte) {
        String normalizado = DocumentoUtil.recompor(contribuinte.getTipoDocumento(),
                new DocumentoDecomposto(contribuinte.getDocCorpo(), contribuinte.getDocFilial(), contribuinte.getDocDigito()));
        return DocumentoUtil.aplicarMascara(contribuinte.getTipoDocumento(), normalizado);
    }

    public String documentoNormalizadoParaLink(Contribuinte contribuinte) {
        return DocumentoUtil.recompor(contribuinte.getTipoDocumento(),
                new DocumentoDecomposto(contribuinte.getDocCorpo(), contribuinte.getDocFilial(), contribuinte.getDocDigito()));
    }

    private void executarPesquisa() {
        resultado = service.pesquisar(filtro);
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, texto, null));
    }

    public FiltroPesquisa getFiltro() {
        return filtro;
    }

    public ResultadoPaginado<Contribuinte> getResultado() {
        return resultado;
    }

    public Contribuinte getContribuinteSelecionadoParaExclusao() {
        return contribuinteSelecionadoParaExclusao;
    }

    public TipoDocumento[] getTiposDocumento() {
        return TipoDocumento.values();
    }
}
