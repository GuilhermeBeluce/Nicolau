package br.com.cadastrocontribuintes.model;

/**
 * Tipo de documento identificador do contribuinte.
 * A escolha do tipo define a mascara, a quantidade de digitos exigida
 * e quais campos condicionais ficam habilitados no formulario.
 */
public enum TipoDocumento {

    CPF(11, "999.999.999-99"),
    CNPJ(14, "99.999.999/9999-99");

    private final int quantidadeDigitos;
    private final String mascara;

    TipoDocumento(int quantidadeDigitos, String mascara) {
        this.quantidadeDigitos = quantidadeDigitos;
        this.mascara = mascara;
    }

    public int getQuantidadeDigitos() {
        return quantidadeDigitos;
    }

    public String getMascara() {
        return mascara;
    }
}
