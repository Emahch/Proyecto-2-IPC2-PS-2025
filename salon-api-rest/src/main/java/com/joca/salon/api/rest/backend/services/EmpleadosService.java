package com.joca.salon.api.rest.backend.services;

import com.joca.salon.api.rest.backend.database.DBEmpleados;
import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.backend.jwt.JwtManager;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author joca
 */
public class EmpleadosService {

    private final DBEmpleados dBEmpleados;

    public EmpleadosService() {
        this.dBEmpleados = new DBEmpleados();
    }

    /**
     * Metodo encargado de intentar un inicio de sesion para un empleado
     *
     * @param credenciales
     * @return JWT token
     * @throws SQLException si ocurre un error en la conexion
     * @throws com.joca.salon.api.rest.backend.exceptions.DataNotFoundException
     * @throws com.joca.salon.api.rest.backend.exceptions.InvalidDataException
     */
    public String iniciarSesion(CredencialesDTO credenciales) throws SQLException, DataNotFoundException, InvalidDataException, PasswordNeedUpdateException {
        credenciales.encriptarContraseña();
        if (!credenciales.esValido()) {
            throw new InvalidDataException("La contraseña debe tener un minimo de 6 caracteres y no debe superar los 50 caracteres");
        }
        CredencialesDTO credencialesAlmacenadas = dBEmpleados.obtenerCredenciales(credenciales.getIdentificador());

        if (!credenciales.getContraseña().equals(credencialesAlmacenadas.getContraseña())) {
            throw new InvalidDataException("El identificador o contraseña son incorrectos");
        }

        JwtManager jwtManager = new JwtManager();
        return jwtManager.generateToken(obtenerEmpleado(credenciales.getIdentificador()));
    }

    /**
     * Obtiene un empleado en base a su identificador
     *
     * @param identificadorEmpleado
     * @return
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public Empleado obtenerEmpleado(String identificadorEmpleado) throws SQLException, DataNotFoundException {
        return dBEmpleados.obtenerEmpleado(identificadorEmpleado);
    }

    /**
     * Registra un empleado en el sistema
     *
     * @param empleado
     * @throws SQLException
     * @throws InvalidDataException
     * @throws com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException
     */
    public void registrarEmpleado(Empleado empleado) throws SQLException, InvalidDataException, DuplicatedDataException {
        if (!empleado.esValido()) {
            throw new InvalidDataException("La información ingresada no es valida");
        }

        if (dBEmpleados.empleadoExiste(empleado.getIdentificador())) {
            if (dBEmpleados.empleadoEstaInactivo(empleado.getIdentificador())) {
                actualizarEmpleado(empleado, empleado.getIdentificador());

                CredencialesDTO credenciales = new CredencialesDTO();
                credenciales.setIdentificador(empleado.getIdentificador());
                credenciales.setContraseña("");
                actualizarContraseña(credenciales);
            } else {
                throw new DuplicatedDataException("El identificador del empleado ya esta en uso");
            }
        } else {
            dBEmpleados.crearEmpleado(empleado);
        }
    }

    /**
     * Actualiza la información de un empleado
     *
     * @param empleado
     * @param identificadorOriginal
     * @throws SQLException
     * @throws InvalidDataException si la información ingresada no es valida
     * @throws DuplicatedDataException si ya existe un usuario con el mismo
     * identificador
     */
    public void actualizarEmpleado(Empleado empleado, String identificadorOriginal) throws SQLException, InvalidDataException, DuplicatedDataException {
        if (!empleado.esValido()) {
            throw new InvalidDataException("La información ingresada no es valida");
        }
        if (dBEmpleados.empleadoExiste(empleado.getIdentificador())) {
            throw new DuplicatedDataException("El identificador del empleado ya esta en uso");
        }
        dBEmpleados.actualizarEmpleado(empleado, identificadorOriginal);
    }

    /**
     * Actualiza la información de un empleado
     *
     * @param credenciales
     * @throws SQLException
     * @throws InvalidDataException si la información ingresada no es valida
     */
    public void actualizarContraseña(CredencialesDTO credenciales) throws SQLException, InvalidDataException {
        if (!credenciales.esValido()) {
            throw new InvalidDataException("La contraseña ingresada no es valida");
        }
        if (!dBEmpleados.empleadoExiste(credenciales.getIdentificador())) {
            throw new InvalidDataException("No se encontro el empleado indicado");
        }
        dBEmpleados.actualizarContraseña(credenciales);
    }

    /**
     * Desactiva un empleado
     *
     * @param identificadorEmpleado
     * @throws SQLException
     * @throws InvalidDataException si el identificador no coincide con ninguno
     * almacenado
     */
    public void desactivarEmpleado(String identificadorEmpleado) throws SQLException, InvalidDataException {
        dBEmpleados.desactivarEmpleado(identificadorEmpleado);
    }
    
    /**
     * Devuelve una lista de todos los empleados activos en el sistema
     * 
     * @return lista de empleados
     * @throws SQLException 
     */
    public List<Empleado> obtenerEmpleados() throws SQLException {
        return dBEmpleados.obtenerEmpleados();
    }
}
