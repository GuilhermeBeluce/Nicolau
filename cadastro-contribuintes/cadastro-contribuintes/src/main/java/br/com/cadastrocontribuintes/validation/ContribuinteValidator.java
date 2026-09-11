package br.com.cadastrocontribuintes.validation;

import br.com.cadastrocontribuintes.dto.ContribuinteFormulario;
import br.com.cadastrocontribuintes.exception.MensagemNegocio;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import br.com.cadastrocontribuintes.util.DddUtil;
import br.com.cadastrocontribuintes.util.DocumentoUtil;
import br.com.cadastrocontribuintes.util.UfUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validador central e reutilizavel de todos os campos do contribuinte.
 * Usado tanto na inclusao quanto na alteracao (mesmas regras, exceto pela
 * chave, que e imutavel e nao passa por aqui).
 *
 * Nao implementa calculo de digito verificador de CPF/CNPJ: fora do escopo.
 */
public final class ContribuinteValidator {

    private static final int NOME_MIN = 3;
    private static final int NOME_MAX = 120;
    private static final int NOME_FANTASIA_MAX = 80;
    private static final int EMAIL_MAX = 100;
    private static final int CIDADE_MAX = 60;
    private static final BigDecimal FATURAMENTO_MAXIMO = new BigDecimal("9999999999999.99");

    private static final Pattern PADRAO_NOME =
            Pattern.compile("^[\\p{L}\\s'.\\-]+$");
    private static final Pattern PADRAO_APENAS_NUMEROS_OU_SIMBOLOS =
            Pattern.compile("^[^\\p{L}]+$");
    private static final Pattern PADRAO_CIDADE =
            Pattern.compile("^[\\p{L}\\s'\\-]+$");
    private static final Pattern PADRAO_EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");

    private ContribuinteValidator() {
    }

    /**
     * Valida todos os campos do formulario e retorna um mapa ordenado de
     * campo -> mensagem. Mapa vazio significa formulario valido.
     */
    public static Map<String, String> validar(ContribuinteFormulario formulario) {
        Map<String, String> erros = new LinkedHashMap<>();

        validarTipoEDocumento(formulario, erros);
        validarNomeRazaoSocial(formulario, erros);
        validarNomeFantasia(formulario, erros);
        validarEmail(formulario, erros);
        validarDdd(formulario, erros);
        validarTelefone(formulario, erros);
        validarCidade(formulario, erros);
        validarUf(formulario, erros);
        validarDataAbertura(formulario, erros);
        validarQuantidadeFuncionarios(formulario, erros);
        validarFaturamentoAnual(formulario, erros);

        return erros;
    }

    private static void validarTipoEDocumento(ContribuinteFormulario f, Map<String, String> erros) {
        if (f.getTipoDocumento() == null) {
            erros.put("tipoDocumento", MensagemNegocio.campoObrigatorio("Tipo de documento"));
            return;
        }

        String bruto = f.getDocumentoBruto();
        if (bruto == null || bruto.isBlank()) {
            erros.put("documento", MensagemNegocio.campoObrigatorio("Documento"));
            return;
        }

        String normalizado = DocumentoUtil.normalizar(bruto);
        if (!DocumentoUtil.possuiQuantidadeDigitosValida(f.getTipoDocumento(), normalizado)) {
            erros.put("documento", MensagemNegocio.formatoInvalido("Documento"));
        }
    }

    private static void validarNomeRazaoSocial(ContribuinteFormulario f, Map<String, String> erros) {
        String nome = f.getRazaoSocialNome();
        if (nome == null || nome.isBlank()) {
            erros.put("razaoSocialNome", MensagemNegocio.campoObrigatorio("Nome / Razão social"));
            return;
        }

        String nomeAparado = nome.trim();
        if (nomeAparado.length() < NOME_MIN || nomeAparado.length() > NOME_MAX) {
            erros.put("razaoSocialNome", MensagemNegocio.formatoInvalido("Nome / Razão social"));
            return;
        }

        if (!PADRAO_NOME.matcher(nomeAparado).matches()) {
            erros.put("razaoSocialNome", MensagemNegocio.formatoInvalido("Nome / Razão social"));
            return;
        }

        boolean cpf = f.getTipoDocumento() == TipoDocumento.CPF;
        if (cpf && PADRAO_APENAS_NUMEROS_OU_SIMBOLOS.matcher(nomeAparado).matches()) {
            erros.put("razaoSocialNome", MensagemNegocio.formatoInvalido("Nome / Razão social"));
        }
    }

    private static void validarNomeFantasia(ContribuinteFormulario f, Map<String, String> erros) {
        if (f.getTipoDocumento() != TipoDocumento.CNPJ) {
            return;
        }
        String nomeFantasia = f.getNomeFantasia();
        if (nomeFantasia != null && nomeFantasia.trim().length() > NOME_FANTASIA_MAX) {
            erros.put("nomeFantasia", MensagemNegocio.formatoInvalido("Nome fantasia"));
        }
    }

    private static void validarEmail(ContribuinteFormulario f, Map<String, String> erros) {
        String email = f.getEmailCorporativo();
        if (email == null || email.isBlank()) {
            erros.put("email", MensagemNegocio.campoObrigatorio("E-mail"));
            return;
        }

        String emailAparado = email.trim();
        if (emailAparado.length() > EMAIL_MAX || !PADRAO_EMAIL.matcher(emailAparado).matches()) {
            erros.put("email", MensagemNegocio.formatoInvalido("E-mail"));
        }
    }

    private static void validarDdd(ContribuinteFormulario f, Map<String, String> erros) {
        String ddd = f.getDddTelefone();
        if (ddd == null || ddd.isBlank()) {
            erros.put("ddd", MensagemNegocio.campoObrigatorio("DDD"));
            return;
        }
        if (!ddd.matches("\\d{2}") || !DddUtil.isValido(ddd)) {
            erros.put("ddd", MensagemNegocio.formatoInvalido("DDD"));
        }
    }

    private static void validarTelefone(ContribuinteFormulario f, Map<String, String> erros) {
        String telefone = f.getNumeroTelefone();
        if (telefone == null || telefone.isBlank()) {
            erros.put("telefone", MensagemNegocio.campoObrigatorio("Telefone"));
            return;
        }

        String normalizado = telefone.replaceAll("\\D", "");
        if (normalizado.length() == 9 && normalizado.startsWith("9")) {
            return;
        }
        if (normalizado.length() == 8) {
            return;
        }
        erros.put("telefone", MensagemNegocio.formatoInvalido("Telefone"));
    }

    private static void validarCidade(ContribuinteFormulario f, Map<String, String> erros) {
        String cidade = f.getCidadeSede();
        if (cidade == null || cidade.isBlank()) {
            erros.put("cidade", MensagemNegocio.campoObrigatorio("Cidade"));
            return;
        }

        String cidadeAparada = cidade.trim();
        if (cidadeAparada.length() > CIDADE_MAX || !PADRAO_CIDADE.matcher(cidadeAparada).matches()) {
            erros.put("cidade", MensagemNegocio.formatoInvalido("Cidade"));
        }
    }

    private static void validarUf(ContribuinteFormulario f, Map<String, String> erros) {
        String uf = f.getEstadoUf();
        if (uf == null || uf.isBlank()) {
            erros.put("uf", MensagemNegocio.campoObrigatorio("UF"));
            return;
        }
        if (!UfUtil.isValida(uf)) {
            erros.put("uf", MensagemNegocio.formatoInvalido("UF"));
        }
    }

    private static void validarDataAbertura(ContribuinteFormulario f, Map<String, String> erros) {
        LocalDate data = f.getDataAbertura();
        if (data == null) {
            erros.put("dataAbertura", MensagemNegocio.campoObrigatorio("Data de abertura"));
            return;
        }
        if (data.isAfter(LocalDate.now())) {
            erros.put("dataAbertura", MensagemNegocio.formatoInvalido("Data de abertura"));
        }
    }

    private static void validarQuantidadeFuncionarios(ContribuinteFormulario f, Map<String, String> erros) {
        if (f.getTipoDocumento() != TipoDocumento.CNPJ) {
            return;
        }
        Integer quantidade = f.getQuantidadeFuncionarios();
        if (quantidade == null || quantidade < 0) {
            erros.put("quantidadeFuncionarios", MensagemNegocio.formatoInvalido("Quantidade de funcionários"));
        }
    }

    private static void validarFaturamentoAnual(ContribuinteFormulario f, Map<String, String> erros) {
        BigDecimal faturamento = f.getFaturamentoAnual();
        if (faturamento == null) {
            erros.put("faturamentoAnual", MensagemNegocio.campoObrigatorio("Faturamento anual"));
            return;
        }
        if (faturamento.scale() > 2
                || faturamento.compareTo(BigDecimal.ZERO) < 0
                || faturamento.compareTo(FATURAMENTO_MAXIMO) > 0) {
            erros.put("faturamentoAnual", MensagemNegocio.formatoInvalido("Faturamento anual"));
        }
    }
}
