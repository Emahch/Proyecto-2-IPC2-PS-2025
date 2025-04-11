package com.joca.salon.api.rest.backend.services;

import com.joca.salon.api.rest.backend.database.DBEmpleados;
import com.joca.salon.api.rest.backend.database.DBUsuarios;
import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author joca
 */
public class EmpleadosService {

    /**
     * Obtiene un empleado en base a su identificador
     *
     * @param identificadorEmpleado
     * @return
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public Empleado obtenerEmpleado(String identificadorEmpleado) throws SQLException, DataNotFoundException {
        DBEmpleados dBEmpleados = new DBEmpleados();
        return dBEmpleados.obtenerEmpleado(identificadorEmpleado);
    }

    /**
     * Registra un empleado en el sistema
     *
     * @param empleado
     * @throws SQLException
     * @throws InvalidDataException
     * @throws
     * com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException
     */
    public void registrarEmpleado(Empleado empleado) throws SQLException, InvalidDataException, DuplicatedDataException {
        if (!empleado.esValido()) {
            throw new InvalidDataException("La información ingresada no es valida");
        }
        
        DBUsuarios dBUsuarios = new DBUsuarios();
        if (dBUsuarios.usuarioExiste(empleado.getIdentificador(), Empleado.class)) {
            if (dBUsuarios.usuarioEstaInactivo(empleado.getIdentificador(), Empleado.class)) {
                actualizarEmpleado(empleado, empleado.getIdentificador());

                CredencialesDTO credenciales = new CredencialesDTO();
                credenciales.setIdentificador(empleado.getIdentificador());
                credenciales.setContraseña("");
                dBUsuarios.actualizarContraseña(credenciales, Empleado.class);
            } else {
                throw new DuplicatedDataException("El identificador del empleado ya esta en uso");
            }
        } else {
            DBEmpleados dBEmpleados = new DBEmpleados();
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
        DBUsuarios dBUsuarios = new DBUsuarios();
        if (dBUsuarios.usuarioExiste(empleado.getIdentificador(), Empleado.class)) {
            throw new DuplicatedDataException("El identificador del empleado ya esta en uso");
        }
        DBEmpleados dBEmpleados = new DBEmpleados();
        dBEmpleados.actualizarEmpleado(empleado, identificadorOriginal);
    }


   

    /**
     * Devuelve una lista de todos los empleados activos en el sistema
     *
     * @return lista de empleados
     * @throws SQLException
     */
    public List<Empleado> obtenerEmpleados() throws SQLException {
        DBEmpleados dBEmpleados = new DBEmpleados();
        return dBEmpleados.obtenerEmpleados();
    }
}
