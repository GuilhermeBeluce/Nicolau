package br.com.cadastrocontribuintes.validation;

import br.com.cadastrocontribuintes.dto.ContribuinteFormulario;
import br.com.cadastrocontribuintes.model.TipoDocumento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContribuinteValidatorTest {

    private ContribuinteFormulario formularioCpfValido() {
        ContribuinteFormulario f = new ContribuinteFormulario();
        f.setTipoDocumento(TipoDocumento.CPF);
        f.setDocumentoBruto("123.456.789-00");
        f.setRazaoSocialNome("Maria da Silva");
        f.setEmailCorporativo("maria@exemplo.com.br");
        f.setDddTelefone("43");
        f.setNumeroTelefone("999990000");
        f.setCidadeSede("Londrina");
        f.setEstadoUf("PR");
        f.setDataAbertura(LocalDate.of(1990, 1, 1));
        f.setFaturamentoAnual(BigDecimal.ZERO);
        return f;
    }

    private ContribuinteFormulario formularioCnpjValido() {
        ContribuinteFormulario f = new ContribuinteFormulario();
        f.setTipoDocumento(TipoDocumento.CNPJ);
        f.setDocumentoBruto("12.345.678/0001-99");
        f.setRazaoSocialNome("Empresa Fictícia Ltda");
        f.setNomeFantasia("Empresa Fictícia");
        f.setEmailCorporativo("contato@exemplo.com.br");
        f.setDddTelefone("43");
        f.setNumeroTelefone("33334444");
        f.setCidadeSede("Londrina");
        f.setEstadoUf("PR");
        f.setDataAbertura(LocalDate.of(2010, 5, 20));
        f.setQuantidadeFuncionarios(10);
        f.setFaturamentoAnual(new BigDecimal("100000.00"));
        return f;
    }

    @Test
    void formularioCpfValidoNaoDeveGerarErros() {
        assertTrue(ContribuinteValidator.validar(formularioCpfValido()).isEmpty());
    }

    @Test
    void formularioCnpjValidoNaoDeveGerarErros() {
        assertTrue(ContribuinteValidator.validar(formularioCnpjValido()).isEmpty());
    }

    @Test
    void deveExigirTipoDeDocumento() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setTipoDocumento(null);
        assertTrue(ContribuinteValidator.validar(f).containsKey("tipoDocumento"));
    }

    @Test
    void deveRejeitarDocumentoComTamanhoInvalido() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setDocumentoBruto("123");
        assertTrue(ContribuinteValidator.validar(f).containsKey("documento"));
    }

    @Test
    void deveRejeitarNomeCurtoDemais() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setRazaoSocialNome("Jo");
        assertTrue(ContribuinteValidator.validar(f).containsKey("razaoSocialNome"));
    }

    @Test
    void deveRejeitarNomeDeCpfFormadoSoPorNumeros() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setRazaoSocialNome("123456789");
        assertTrue(ContribuinteValidator.validar(f).containsKey("razaoSocialNome"));
    }

    @Test
    void deveAceitarNomeComAcentoApostrofoEHifen() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setRazaoSocialNome("D'Ávila Pereira-Souza");
        assertFalse(ContribuinteValidator.validar(f).containsKey("razaoSocialNome"));
    }

    @Test
    void deveRejeitarEmailSemArroba() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setEmailCorporativo("mariaexemplo.com.br");
        assertTrue(ContribuinteValidator.validar(f).containsKey("email"));
    }

    @Test
    void deveRejeitarDddInvalido() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setDddTelefone("00");
        assertTrue(ContribuinteValidator.validar(f).containsKey("ddd"));
    }

    @Test
    void deveRejeitarCelularQueNaoComecaCom9() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setNumeroTelefone("888880000");
        assertTrue(ContribuinteValidator.validar(f).containsKey("telefone"));
    }

    @Test
    void deveAceitarTelefoneFixoComOitoDigitos() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setNumeroTelefone("33334444");
        assertFalse(ContribuinteValidator.validar(f).containsKey("telefone"));
    }

    @Test
    void deveRejeitarCidadeComNumero() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setCidadeSede("Londrina2");
        assertTrue(ContribuinteValidator.validar(f).containsKey("cidade"));
    }

    @Test
    void deveRejeitarUfInexistente() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setEstadoUf("ZZ");
        assertTrue(ContribuinteValidator.validar(f).containsKey("uf"));
    }

    @Test
    void deveRejeitarDataDeAberturaFutura() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setDataAbertura(LocalDate.now().plusDays(1));
        assertTrue(ContribuinteValidator.validar(f).containsKey("dataAbertura"));
    }

    @Test
    void deveRejeitarQuantidadeDeFuncionariosNegativaParaCnpj() {
        ContribuinteFormulario f = formularioCnpjValido();
        f.setQuantidadeFuncionarios(-1);
        assertTrue(ContribuinteValidator.validar(f).containsKey("quantidadeFuncionarios"));
    }

    @Test
    void deveIgnorarQuantidadeDeFuncionariosParaCpf() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setQuantidadeFuncionarios(null);
        Map<String, String> erros = ContribuinteValidator.validar(f);
        assertFalse(erros.containsKey("quantidadeFuncionarios"));
    }

    @Test
    void deveRejeitarFaturamentoNegativo() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setFaturamentoAnual(new BigDecimal("-1.00"));
        assertTrue(ContribuinteValidator.validar(f).containsKey("faturamentoAnual"));
    }

    @Test
    void deveRejeitarFaturamentoComMaisDeDuasCasasDecimais() {
        ContribuinteFormulario f = formularioCpfValido();
        f.setFaturamentoAnual(new BigDecimal("100.123"));
        assertTrue(ContribuinteValidator.validar(f).containsKey("faturamentoAnual"));
    }

    @Test
    void deveRejeitarNomeFantasiaMuitoLongoParaCnpj() {
        ContribuinteFormulario f = formularioCnpjValido();
        f.setNomeFantasia("A".repeat(81));
        assertTrue(ContribuinteValidator.validar(f).containsKey("nomeFantasia"));
    }
}
