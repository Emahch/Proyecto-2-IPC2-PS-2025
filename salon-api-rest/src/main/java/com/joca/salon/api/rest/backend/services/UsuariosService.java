package com.joca.salon.api.rest.backend.services;

import com.joca.salon.api.rest.backend.database.DBUsuarios;
import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.backend.exceptions.UserNotValidException;
import com.joca.salon.api.rest.backend.jwt.JwtManager;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.Usuario;
import com.joca.salon.api.rest.model.usuarios.clientes.Cliente;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import java.sql.SQLException;

/**
 *
 * @author joca
 * @param <T>
 */
public class UsuariosService {

    /**
     * Metodo encargado de intentar un inicio de sesion para un usuario
     *
     * @param credenciales
     * @param T
     * @return JWT token
     * @throws SQLException si ocurre un error en la conexion
     * @throws com.joca.salon.api.rest.backend.exceptions.DataNotFoundException
     * @throws com.joca.salon.api.rest.backend.exceptions.InvalidDataException
     * @throws
     * com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException
     * @throws com.joca.salon.api.rest.backend.exceptions.UserNotValidException
     */
    public String iniciarSesion(CredencialesDTO credenciales, Class T) throws SQLException, DataNotFoundException, InvalidDataException, PasswordNeedUpdateException, UserNotValidException {
        credenciales.encriptarContraseña();
        if (!credenciales.esValido()) {
            throw new InvalidDataException("La contraseña debe tener un minimo de 6 caracteres y no debe superar los 50 caracteres");
        }
        DBUsuarios dBUsuarios = new DBUsuarios();
        CredencialesDTO credencialesAlmacenadas = dBUsuarios.obtenerCredenciales(credenciales.getIdentificador(), T);

        if (!credenciales.getContraseña().equals(credencialesAlmacenadas.getContraseña())) {
            throw new InvalidDataException("El identificador o contraseña son incorrectos");
        }

        JwtManager jwtManager = new JwtManager();
        Usuario usuario;
        if (T.getCanonicalName().equals(Empleado.class.getCanonicalName())) {
            EmpleadosService empleadosService = new EmpleadosService();
            usuario = empleadosService.obtenerEmpleado(credenciales.getIdentificador());
        } else if (T.getCanonicalName().equals(Cliente.class.getCanonicalName())) {
            ClientesService clientesService = new ClientesService();
            usuario = clientesService.obtenerCliente(credenciales.getIdentificador(), "correo");
        } else {
            throw new UserNotValidException("El usuario no es valido");
        }
        return jwtManager.generateToken(usuario);
    }

    /**
     * Actualiza la información de un usuario
     *
     * @param credenciales
     * @param T
     * @throws SQLException
     * @throws InvalidDataException si la información ingresada no es valida
     */
    public void actualizarContraseña(CredencialesDTO credenciales, Class T) throws SQLException, InvalidDataException {
        DBUsuarios dBUsuarios = new DBUsuarios();
        credenciales.encriptarContraseña();
        if (!credenciales.esValido()) {
            throw new InvalidDataException("La contraseña ingresada no es valida");
        }
        if (!dBUsuarios.usuarioExiste(credenciales.getIdentificador(), T)) {
            throw new InvalidDataException("No se encontro el usuario indicado");
        }
        dBUsuarios.actualizarContraseña(credenciales, T);
    }

    /**
     * Desactiva un usuario
     *
     * @param identificador
     * @param estado
     * @param T
     * @throws SQLException
     * @throws InvalidDataException si el identificador no coincide con ninguno
     * almacenado
     */
    public void editarEstadoUsuario(String identificador, EstadoEnum estado, Class T) throws SQLException, InvalidDataException {
        DBUsuarios dbUsuarios = new DBUsuarios();
        dbUsuarios.editarEstadoUsuario(identificador, estado, T);
    }
}
