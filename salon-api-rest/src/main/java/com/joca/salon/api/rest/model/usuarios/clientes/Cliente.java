package com.joca.salon.api.rest.model.usuarios.clientes;

import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.Usuario;

/**
 *
 * @author joca
 */
public class Cliente extends Usuario {

    public static final int MAX_LENGTH_MAIL = 50;

    private String correo;
    private EstadoEnum estado;
    private String direccion;
    private String telefono;
    private String descripcion;
    private String hobbies;

    @Override
    public boolean esValido() {
        return identificador != null && identificador.length() == LENGTH_ID && !identificador.contains(" ")
                && nombre != null && nombre.length() <= MAX_LENGTH_NOMBRE && nombre.length() >= MIN_LENGTH_NOMBRE
                && correo != null && correo.length() <= MAX_LENGTH_MAIL && esCorreoValido(correo)
                && (telefono == null || telefono.matches("^\\d+$"));
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public EstadoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    private boolean esCorreoValido(String correo) {
        return correo.matches("^(?=.{1,64}@)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
