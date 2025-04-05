/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joca.salon.api.rest.model.usuarios.empleados;

import com.joca.salon.api.rest.model.usuarios.Usuario;

/**
 *
 * @author joca
 */
public class Empleado extends Usuario {

    public static final int LENGTH_ID = 13;
    public static final int MAX_LENGTH_DESCRIPCION = 200;

    private RolEnum rol;
    private byte[] fotografia;
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

    public byte[] getFotografia() {
        return fotografia;
    }

    public void setFotografia(byte[] fotografia) {
        this.fotografia = fotografia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
}
