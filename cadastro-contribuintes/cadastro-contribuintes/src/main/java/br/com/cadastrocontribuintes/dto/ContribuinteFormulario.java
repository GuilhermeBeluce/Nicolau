package br.com.cadastrocontribuintes.dto;

import br.com.cadastrocontribuintes.model.TipoDocumento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados brutos capturados no formulario de inclusao ou alteracao, ainda sem
 * normalizacao. E o objeto trafegado entre o Managed Bean e o Service.
 */
public class ContribuinteFormulario implements Serializable {

    private TipoDocumento tipoDocumento;
    private String documentoBruto;
    private String razaoSocialNome;
    private String nomeFantasia;
    private String dddTelefone;
    private String numeroTelefone;
    private String emailCorporativo;
    private String cidadeSede;
    private String estadoUf;
    private LocalDate dataAbertura;
    private Integer quantidadeFuncionarios;
    private BigDecimal faturamentoAnual;

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumentoBruto() {
        return documentoBruto;
    }

    public void setDocumentoBruto(String documentoBruto) {
        this.documentoBruto = documentoBruto;
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

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
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

    public boolean isCnpj() {
        return tipoDocumento == TipoDocumento.CNPJ;
    }
}
