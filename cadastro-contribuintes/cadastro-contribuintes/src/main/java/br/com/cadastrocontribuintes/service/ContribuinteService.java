package br.com.cadastrocontribuintes.service;

import br.com.cadastrocontribuintes.dto.ContribuinteFormulario;
import br.com.cadastrocontribuintes.dto.FiltroPesquisa;
import br.com.cadastrocontribuintes.dto.ResultadoOperacao;
import br.com.cadastrocontribuintes.dto.ResultadoPaginado;
import br.com.cadastrocontribuintes.exception.ValidacaoException;
import br.com.cadastrocontribuintes.model.Contribuinte;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.repository.ContribuinteRepository;
import br.com.cadastrocontribuintes.util.DocumentoDecomposto;
import br.com.cadastrocontribuintes.util.DocumentoUtil;
import br.com.cadastrocontribuintes.validation.ContribuinteValidator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Optional;

/**
 * Coordena as regras de negocio do Cadastro de Contribuintes: normaliza
 * dados, valida, decompoe/recompoe documentos e delega a persistencia ao
 * Repository. Nao conhece detalhes de JSF (FacesContext, FacesMessage) nem
 * de SQL.
 */
@ApplicationScoped
public class ContribuinteService {

    @Inject
    private ContribuinteRepository repository;

    public ResultadoOperacao incluir(ContribuinteFormulario formulario) {
        validarOuFalhar(formulario);

        TipoDocumento tipo = formulario.getTipoDocumento();
        String documentoNormalizado = DocumentoUtil.normalizar(formulario.getDocumentoBruto());
        DocumentoDecomposto chave = DocumentoUtil.decompor(tipo, documentoNormalizado);

        Contribuinte contribuinte = montarContribuinte(formulario, chave);
        return repository.incluir(contribuinte);
    }

    public ResultadoOperacao alterar(DocumentoDecomposto chaveOriginal, ContribuinteFormulario formulario) {
        validarOuFalhar(formulario);

        Contribuinte dadosEditaveis = montarContribuinte(formulario, chaveOriginal);
        return repository.alterar(chaveOriginal, dadosEditaveis);
    }

    public Optional<Contribuinte> detalhar(TipoDocumento tipo, String documentoBruto) {
        String documentoNormalizado = DocumentoUtil.normalizar(documentoBruto);
        if (!DocumentoUtil.possuiQuantidadeDigitosValida(tipo, documentoNormalizado)) {
            return Optional.empty();
        }
        DocumentoDecomposto chave = DocumentoUtil.decompor(tipo, documentoNormalizado);
        return repository.detalhar(chave);
    }

    /**
     * Pesquisa paginada. Se a pagina solicitada superar a ultima pagina
     * valida (por exemplo, apos exclusoes), a pesquisa e refeita na ultima
     * pagina existente.
     */
    public ResultadoPaginado<Contribuinte> pesquisar(FiltroPesquisa filtro) {
        filtro.setDocumento(DocumentoUtil.normalizar(filtro.getDocumento()));
        if (filtro.getNomeRazaoSocial() != null) {
            filtro.setNomeRazaoSocial(filtro.getNomeRazaoSocial().trim());
        }

        ResultadoPaginado<Contribuinte> resultado = repository.pesquisar(filtro);

        boolean paginaSolicitadaInvalida = resultado.getTotalRegistros() > 0
                && filtro.getPagina() > resultado.getTotalPaginas();

        if (paginaSolicitadaInvalida) {
            filtro.setPagina(resultado.getTotalPaginas());
            resultado = repository.pesquisar(filtro);
        }

        return resultado;
    }

    public ResultadoOperacao excluir(DocumentoDecomposto chave) {
        return repository.excluir(chave);
    }

    private void validarOuFalhar(ContribuinteFormulario formulario) {
        Map<String, String> erros = ContribuinteValidator.validar(formulario);
        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
    }

    private Contribuinte montarContribuinte(ContribuinteFormulario formulario, DocumentoDecomposto chave) {
        TipoDocumento tipo = formulario.getTipoDocumento();
        boolean cnpj = tipo == TipoDocumento.CNPJ;

        Contribuinte contribuinte = new Contribuinte();
        contribuinte.setDocCorpo(chave.docCorpo());
        contribuinte.setDocFilial(chave.docFilial());
        contribuinte.setDocDigito(chave.docDigito());
        contribuinte.setTipoDocumento(tipo);
        contribuinte.setRazaoSocialNome(formulario.getRazaoSocialNome().trim());
        contribuinte.setNomeFantasia(cnpj ? normalizarOpcional(formulario.getNomeFantasia()) : null);
        contribuinte.setQuantidadeFuncionarios(cnpj ? formulario.getQuantidadeFuncionarios() : 0);
        contribuinte.setFaturamentoAnual(formulario.getFaturamentoAnual());
        contribuinte.setDataAbertura(formulario.getDataAbertura());
        contribuinte.setDddTelefone(formulario.getDddTelefone());
        contribuinte.setNumeroTelefone(formulario.getNumeroTelefone().replaceAll("\\D", ""));
        contribuinte.setEmailCorporativo(formulario.getEmailCorporativo().trim().toLowerCase());
        contribuinte.setCidadeSede(formulario.getCidadeSede().trim());
        contribuinte.setEstadoUf(formulario.getEstadoUf().trim().toUpperCase());
        return contribuinte;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
