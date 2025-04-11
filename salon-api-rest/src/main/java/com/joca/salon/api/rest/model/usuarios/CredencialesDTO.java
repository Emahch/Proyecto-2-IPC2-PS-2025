package com.joca.salon.api.rest.model.usuarios;

import java.math.BigInteger;

/**
 *
 * @author joca
 */
public class CredencialesDTO {

    /*
     * Los valores de longitud se multiplican por 2, debido a la encriptación
     */
    public static final int MAX_LENGTH_PASSWORD = 50 * 2; // 50 caracteres
    public static final int MIN_LENGTH_PASSWORD = 6 * 2; // 6 caracteres

    private String identificador;
    private String contraseña;

    public CredencialesDTO() {
    }

    public void encriptarContraseña() {
        this.contraseña = String.format("%x", new BigInteger(1, contraseña.getBytes()));
    }

    public boolean esValido() {
        return this.contraseña != null && this.contraseña.length() >= MIN_LENGTH_PASSWORD && this.contraseña.length() <= MAX_LENGTH_PASSWORD;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getContraseña() {
        return contraseña;
    }

}
