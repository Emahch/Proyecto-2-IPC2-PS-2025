package com.joca.salon.api.rest.model.usuarios;

import com.joca.salon.api.rest.model.Almacenable;

/**
 *
 * @author joca
 */
public abstract class Usuario implements Almacenable {

    public static final int MAX_LENGTH_NOMBRE = 50;
    public static final int MIN_LENGTH_NOMBRE = 3;

    protected String identificador;
    protected String nombre;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
