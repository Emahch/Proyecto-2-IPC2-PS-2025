package com.joca.salon.api.rest.model.usuarios.empleados;

import com.joca.salon.api.rest.model.usuarios.Usuario;

/**
 *
 * @author joca
 */
public class Empleado extends Usuario {
    public static final int MAX_LENGTH_DESCRIPCION = 200;

    private RolEnum rol;
    private String descripcion;

    @Override
    public boolean esValido() {
        return identificador != null && identificador.length() == LENGTH_ID && !identificador.contains(" ")
                && nombre != null && nombre.length() <= MAX_LENGTH_NOMBRE && nombre.length() >= MIN_LENGTH_NOMBRE
                && rol != null && (descripcion == null || descripcion.length() <= MAX_LENGTH_DESCRIPCION);
    }

    public RolEnum getRol() {
        return rol;
    }

    public void setRol(RolEnum rol) {
        this.rol = rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
}
