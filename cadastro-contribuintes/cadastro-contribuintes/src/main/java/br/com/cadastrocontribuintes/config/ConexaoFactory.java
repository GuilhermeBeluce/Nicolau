package br.com.cadastrocontribuintes.config;

import br.com.cadastrocontribuintes.exception.ConfiguracaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Ponto unico de criacao de conexoes JDBC com o MySQL. A URL, o usuario e a
 * senha nunca sao gravados no codigo: sao lidos das variaveis de ambiente
 * DB_URL, DB_USER e DB_PASSWORD (ver README para configuracao no IntelliJ).
 *
 * Cada Repository deve abrir sua conexao por meio desta classe e sempre em
 * bloco try-with-resources, nunca reaproveitando ou compartilhando conexoes
 * entre chamadas.
 */
public final class ConexaoFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConexaoFactory.class);

    private static final String VAR_URL = "DB_URL";
    private static final String VAR_USUARIO = "DB_USER";
    private static final String VAR_SENHA = "DB_PASSWORD";

    private ConexaoFactory() {
    }

    public static Connection obterConexao() {
        String url = valorObrigatorio(VAR_URL);
        String usuario = valorObrigatorio(VAR_USUARIO);
        String senha = valorObrigatorio(VAR_SENHA);

        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            LOG.error("Falha ao conectar ao MySQL.", e);
            throw new ConfiguracaoException(
                    "Não foi possível conectar ao banco de dados. Verifique DB_URL, DB_USER e DB_PASSWORD.");
        }
    }

    private static String valorObrigatorio(String nomeVariavel) {
        String valor = System.getenv(nomeVariavel);
        if (valor == null || valor.isBlank()) {
            throw new ConfiguracaoException(
                    "Variável de ambiente obrigatória não definida: " + nomeVariavel
                            + ". Consulte o README para configurá-la no IntelliJ IDEA.");
        }
        return valor;
    }
}
