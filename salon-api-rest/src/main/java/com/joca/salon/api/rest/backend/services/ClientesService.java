package com.joca.salon.api.rest.backend.services;

import com.joca.salon.api.rest.backend.database.DBClientes;
import com.joca.salon.api.rest.backend.database.DBUsuarios;
import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.clientes.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author joca
 */
public class ClientesService {

    /**
     * Obtiene un cliente en base a su identificador
     *
     * @param identificadorCliente
     * @return
     * @throws SQLException
     * @throws DataNotFoundException
     */
    public Cliente obtenerCliente(String identificadorCliente, String tipoID) throws SQLException, DataNotFoundException {
        DBClientes dBClientes = new DBClientes();
        return dBClientes.obtenerCliente(identificadorCliente, tipoID);
    }

    /**
     * Registra un cliente en el sistema
     *
     * @param cliente
     * @throws SQLException
     * @throws InvalidDataException
     * @throws
     * com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException
     */
    public void registrarCliente(Cliente cliente) throws SQLException, InvalidDataException, DuplicatedDataException {
        if (!cliente.esValido()) {
            throw new InvalidDataException("La información ingresada no es valida");
        }

        DBUsuarios dBUsuarios = new DBUsuarios();
        if (dBUsuarios.usuarioExiste(cliente.getIdentificador(), Cliente.class)) {
            if (dBUsuarios.usuarioEstaInactivo(cliente.getIdentificador(), Cliente.class)) {
                cliente.setEstado(EstadoEnum.ACTIVO);
                actualizarCliente(cliente, cliente.getIdentificador());

                CredencialesDTO credenciales = new CredencialesDTO();
                credenciales.setIdentificador(cliente.getIdentificador());
                credenciales.setContraseña("");
                dBUsuarios.actualizarContraseña(credenciales, Cliente.class);
            } else {
                throw new DuplicatedDataException("El identificador ingresado ya esta en uso");
            }
        } else {
            DBClientes dBClientes = new DBClientes();
            if (dBClientes.correoEstaEnUso(cliente.getCorreo())) {
                throw new DuplicatedDataException("El correo ingresado ya esta en uso");
            }
            dBClientes.crearCliente(cliente);
        }
    }

    /**
     * Actualiza la información de un cliente
     *
     * @param cliente
     * @param identificadorOriginal
     * @throws SQLException
     * @throws InvalidDataException si la información ingresada no es valida
     * @throws DuplicatedDataException si ya existe un usuario con el mismo
     * identificador
     */
    public void actualizarCliente(Cliente cliente, String identificadorOriginal) throws SQLException, InvalidDataException, DuplicatedDataException {
        if (!cliente.esValido()) {
            throw new InvalidDataException("La información ingresada no es valida");
        }
        DBUsuarios dBUsuarios = new DBUsuarios();
        if (dBUsuarios.usuarioExiste(cliente.getIdentificador(), Cliente.class)) {
            throw new DuplicatedDataException("El identificador ingresado ya esta en uso");
        }
        DBClientes dBClientes = new DBClientes();
        if (dBClientes.correoEstaEnUso(cliente.getCorreo())) {
            throw new DuplicatedDataException("El correo ingresado ya esta en uso");
        }
        dBClientes.actualizarCliente(cliente, identificadorOriginal);
    }

    /**
     * Devuelve una lista de todos los clientes activos en el sistema
     *
     * @return lista de clientes
     * @throws SQLException
     */
    public List<Cliente> obtenerClientes() throws SQLException {
        DBClientes dBClientes = new DBClientes();
        return dBClientes.obtenerClientes();
    }
}
