package br.com.cadastrocontribuintes.repository;

import br.com.cadastrocontribuintes.config.ConexaoFactory;
import br.com.cadastrocontribuintes.dto.FiltroPesquisa;
import br.com.cadastrocontribuintes.dto.ResultadoOperacao;
import br.com.cadastrocontribuintes.dto.ResultadoPaginado;
import br.com.cadastrocontribuintes.exception.PersistenciaException;
import br.com.cadastrocontribuintes.model.Contribuinte;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.util.DocumentoDecomposto;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unico componente Java autorizado a acessar o MySQL. Toda comunicacao com o
 * banco ocorre exclusivamente por CallableStatement chamando Stored Procedures;
 * nenhum SQL de negocio (INSERT/UPDATE/DELETE/SELECT) e escrito aqui.
 *
 * Nao contem regras visuais, FacesMessage, FacesContext, navegacao ou
 * formatacao de exibicao: essas responsabilidades pertencem a outras camadas.
 */
@ApplicationScoped
public class ContribuinteRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ContribuinteRepository.class);

    private static final String MENSAGEM_FALHA_TECNICA =
            "Não foi possível concluir a operação. Tente novamente.";

    public ResultadoOperacao incluir(Contribuinte contribuinte) {
        String chamada = "{call sp_contribuinte_incluir(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        try (Connection conexao = ConexaoFactory.obterConexao();
             CallableStatement cs = conexao.prepareCall(chamada)) {

            cs.setString(1, contribuinte.getDocCorpo());
            cs.setString(2, contribuinte.getDocFilial());
            cs.setString(3, contribuinte.getDocDigito());
            cs.setString(4, contribuinte.getTipoDocumento().name());
            cs.setInt(5, contribuinte.getQuantidadeFuncionarios());
            cs.setBigDecimal(6, contribuinte.getFaturamentoAnual());
            cs.setDate(7, Date.valueOf(contribuinte.getDataAbertura()));
            cs.setString(8, contribuinte.getRazaoSocialNome());
            cs.setString(9, contribuinte.getNomeFantasia());
            cs.setString(10, contribuinte.getDddTelefone());
            cs.setString(11, contribuinte.getNumeroTelefone());
            cs.setString(12, contribuinte.getEmailCorporativo());
            cs.setString(13, contribuinte.getCidadeSede());
            cs.setString(14, contribuinte.getEstadoUf());
            cs.registerOutParameter(15, Types.VARCHAR);
            cs.registerOutParameter(16, Types.VARCHAR);

            cs.execute();

            String codigo = cs.getString(15);
            String mensagem = cs.getString(16);
            return new ResultadoOperacao(codigo, mensagem, codigo.startsWith("OK-") ? 1 : 0);

        } catch (SQLException e) {
            LOG.error("Falha ao incluir contribuinte.", e);
            throw new PersistenciaException(MENSAGEM_FALHA_TECNICA, e);
        }
    }

    public ResultadoOperacao alterar(DocumentoDecomposto chaveOriginal, Contribuinte dadosEditaveis) {
        String chamada = "{call sp_contribuinte_alterar(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        try (Connection conexao = ConexaoFactory.obterConexao();
             CallableStatement cs = conexao.prepareCall(chamada)) {

            cs.setString(1, chaveOriginal.docCorpo());
            cs.setString(2, chaveOriginal.docFilial());
            cs.setString(3, chaveOriginal.docDigito());
            cs.setInt(4, dadosEditaveis.getQuantidadeFuncionarios());
            cs.setBigDecimal(5, dadosEditaveis.getFaturamentoAnual());
            cs.setDate(6, Date.valueOf(dadosEditaveis.getDataAbertura()));
            cs.setString(7, dadosEditaveis.getRazaoSocialNome());
            cs.setString(8, dadosEditaveis.getNomeFantasia());
            cs.setString(9, dadosEditaveis.getDddTelefone());
            cs.setString(10, dadosEditaveis.getNumeroTelefone());
            cs.setString(11, dadosEditaveis.getEmailCorporativo());
            cs.setString(12, dadosEditaveis.getCidadeSede());
            cs.setString(13, dadosEditaveis.getEstadoUf());
            cs.registerOutParameter(14, Types.VARCHAR);
            cs.registerOutParameter(15, Types.VARCHAR);
            cs.registerOutParameter(16, Types.INTEGER);

            cs.execute();

            return new ResultadoOperacao(cs.getString(14), cs.getString(15), cs.getInt(16));

        } catch (SQLException e) {
            LOG.error("Falha ao alterar contribuinte.", e);
            throw new PersistenciaException(MENSAGEM_FALHA_TECNICA, e);
        }
    }

    public Optional<Contribuinte> detalhar(DocumentoDecomposto chave) {
        String chamada = "{call sp_contribuinte_detalhar(?,?,?)}";

        try (Connection conexao = ConexaoFactory.obterConexao();
             CallableStatement cs = conexao.prepareCall(chamada)) {

            cs.setString(1, chave.docCorpo());
            cs.setString(2, chave.docFilial());
            cs.setString(3, chave.docDigito());

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearContribuinte(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            LOG.error("Falha ao detalhar contribuinte.", e);
            throw new PersistenciaException(MENSAGEM_FALHA_TECNICA, e);
        }
    }

    public ResultadoPaginado<Contribuinte> pesquisar(FiltroPesquisa filtro) {
        String chamada = "{call sp_contribuinte_pesquisar(?,?,?,?,?)}";

        try (Connection conexao = ConexaoFactory.obterConexao();
             CallableStatement cs = conexao.prepareCall(chamada)) {

            cs.setString(1, vazioParaNulo(filtro.getDocumento()));
            cs.setString(2, vazioParaNulo(filtro.getNomeRazaoSocial()));
            cs.setInt(3, filtro.getPagina());
            cs.setInt(4, filtro.getTamanhoPagina());
            cs.registerOutParameter(5, Types.INTEGER);

            List<Contribuinte> registros = new ArrayList<>();
            boolean haResultSet = cs.execute();
            while (haResultSet) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        registros.add(mapearContribuinte(rs));
                    }
                }
                haResultSet = cs.getMoreResults();
            }

            long total = cs.getInt(5);
            return new ResultadoPaginado<>(registros, total, filtro.getPagina(), filtro.getTamanhoPagina());

        } catch (SQLException e) {
            LOG.error("Falha ao pesquisar contribuintes.", e);
            throw new PersistenciaException(MENSAGEM_FALHA_TECNICA, e);
        }
    }

    public ResultadoOperacao excluir(DocumentoDecomposto chave) {
        String chamada = "{call sp_contribuinte_excluir(?,?,?,?,?,?)}";

        try (Connection conexao = ConexaoFactory.obterConexao();
             CallableStatement cs = conexao.prepareCall(chamada)) {

            cs.setString(1, chave.docCorpo());
            cs.setString(2, chave.docFilial());
            cs.setString(3, chave.docDigito());
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();

            return new ResultadoOperacao(cs.getString(4), cs.getString(5), cs.getInt(6));

        } catch (SQLException e) {
            LOG.error("Falha ao excluir contribuinte.", e);
            throw new PersistenciaException(MENSAGEM_FALHA_TECNICA, e);
        }
    }

    private Contribuinte mapearContribuinte(ResultSet rs) throws SQLException {
        Contribuinte contribuinte = new Contribuinte();
        contribuinte.setDocCorpo(rs.getString("doc_corpo"));
        contribuinte.setDocFilial(rs.getString("doc_filial"));
        contribuinte.setDocDigito(rs.getString("doc_digito"));
        contribuinte.setTipoDocumento(TipoDocumento.valueOf(rs.getString("tipo_documento")));
        contribuinte.setQuantidadeFuncionarios(rs.getInt("quantidade_funcionarios"));
        contribuinte.setFaturamentoAnual(rs.getBigDecimal("faturamento_anual"));
        contribuinte.setDataAbertura(rs.getDate("data_abertura").toLocalDate());
        contribuinte.setRazaoSocialNome(rs.getString("razao_social_nome"));
        contribuinte.setNomeFantasia(rs.getString("nome_fantasia"));
        contribuinte.setDddTelefone(rs.getString("ddd_telefone"));
        contribuinte.setNumeroTelefone(rs.getString("numero_telefone"));
        contribuinte.setEmailCorporativo(rs.getString("email_corporativo"));
        contribuinte.setCidadeSede(rs.getString("cidade_sede"));
        contribuinte.setEstadoUf(rs.getString("estado_uf"));
        return contribuinte;
    }

    private String vazioParaNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }
}
