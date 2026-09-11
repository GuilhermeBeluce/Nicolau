package br.com.cadastrocontribuintes.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um contribuinte (pessoa fisica ou juridica) tal como persistido
 * na tabela cadastro_contribuintes. Mantem apenas dados de dominio, sem
 * formatacao de exibicao (mascaras, moeda, data) - essa responsabilidade
 * pertence ao FormatadorUtil.
 */
public class Contribuinte implements Serializable {

    private String docCorpo;
    private String docFilial;
    private String docDigito;
    private TipoDocumento tipoDocumento;
    private Integer quantidadeFuncionarios;
    private BigDecimal faturamentoAnual;
    private LocalDate dataAbertura;
    private String razaoSocialNome;
    private String nomeFantasia;
    private String dddTelefone;
    private String numeroTelefone;
    private String emailCorporativo;
    private String cidadeSede;
    private String estadoUf;

    public Contribuinte() {
    }

    public String getDocCorpo() {
        return docCorpo;
    }

    public void setDocCorpo(String docCorpo) {
        this.docCorpo = docCorpo;
    }

    public String getDocFilial() {
        return docFilial;
    }

    public void setDocFilial(String docFilial) {
        this.docFilial = docFilial;
    }

    public String getDocDigito() {
        return docDigito;
    }

    public void setDocDigito(String docDigito) {
        this.docDigito = docDigito;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getQuantidadeFuncionarios() {
        return quantidadeFuncionarios;
    }

    public void setQuantidadeFuncionarios(Integer quantidadeFuncionarios) {
        this.quantidadeFuncionarios = quantidadeFuncionarios;
    }

    public BigDecimal getFaturamentoAnual() {
        return faturamentoAnual;
    }

    public void setFaturamentoAnual(BigDecimal faturamentoAnual) {
        this.faturamentoAnual = faturamentoAnual;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getRazaoSocialNome() {
        return razaoSocialNome;
    }

    public void setRazaoSocialNome(String razaoSocialNome) {
        this.razaoSocialNome = razaoSocialNome;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getDddTelefone() {
        return dddTelefone;
    }

    public void setDddTelefone(String dddTelefone) {
        this.dddTelefone = dddTelefone;
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public void setEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public String getCidadeSede() {
        return cidadeSede;
    }

    public void setCidadeSede(String cidadeSede) {
        this.cidadeSede = cidadeSede;
    }

    public String getEstadoUf() {
        return estadoUf;
    }

    public void setEstadoUf(String estadoUf) {
        this.estadoUf = estadoUf;
    }

    /**
     * Documento completo, sem mascara, recomposto a partir da chave fracionada.
     * CPF: docCorpo (9) + docDigito (2) = 11 digitos.
     * CNPJ: docCorpo (8) + docFilial (4) + docDigito (2) = 14 digitos.
     */
    public String getDocumentoCompletoNormalizado() {
        if (tipoDocumento == TipoDocumento.CPF) {
            return docCorpo + docDigito;
        }
        return docCorpo + docFilial + docDigito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contribuinte outro)) {
            return false;
        }
        return Objects.equals(docCorpo, outro.docCorpo)
                && Objects.equals(docFilial, outro.docFilial)
                && Objects.equals(docDigito, outro.docDigito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docCorpo, docFilial, docDigito);
    }
}
