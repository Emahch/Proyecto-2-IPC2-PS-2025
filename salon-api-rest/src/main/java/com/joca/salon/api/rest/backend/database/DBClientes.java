package com.joca.salon.api.rest.backend.database;

import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.clientes.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joca
 */
public class DBClientes extends DBConnection {

    /**
     * Obtiene un cliente en base a su identificador
     *
     * @param identificadorCliente
     * @param tipoId
     * @return cliente
     * @throws SQLException
     * @throws DataNotFoundException si no se encuentra ningun cliente
     */
    public Cliente obtenerCliente(String identificadorCliente, String tipoId) throws SQLException, DataNotFoundException {
        String statement = "SELECT * FROM clientes WHERE " + tipoId + " = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificadorCliente);

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdentificador(result.getString("dpi"));
                    cliente.setNombre(result.getString("nombre"));
                    cliente.setCorreo(result.getString("correo"));
                    cliente.setTelefono(result.getString("telefono"));
                    cliente.setDireccion(result.getString("direccion"));
                    cliente.setHobbies(result.getString("hobbies"));
                    cliente.setDescripcion(result.getString("descripcion"));
                    cliente.setEstado(EstadoEnum.valueOf(result.getString("estado")));
                    return cliente;
                }
                throw new DataNotFoundException("No se encontro el cliente, verifica que la información sea correcta");
            }
        }
    }

    /**
     * Obtiene todos los clientes activos en la base de datos
     *
     * @return Lista Clientes
     * @throws SQLException
     */
    public List<Cliente> obtenerClientes() throws SQLException {
        String statement = "SELECT * FROM clientes WHERE estado = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, EstadoEnum.ACTIVO.name());

            try (ResultSet result = st.executeQuery()) {
                List<Cliente> clientes = new ArrayList<>();
                while (result.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdentificador(result.getString("dpi"));
                    cliente.setNombre(result.getString("nombre"));
                    cliente.setCorreo(result.getString("correo"));
                    cliente.setTelefono(result.getString("telefono"));
                    cliente.setDireccion(result.getString("direccion"));
                    cliente.setHobbies(result.getString("hobbies"));
                    cliente.setDescripcion(result.getString("descripcion"));
                    cliente.setEstado(EstadoEnum.valueOf(result.getString("estado")));
                    clientes.add(cliente);
                }
                return clientes;
            }
        }
    }

    /**
     * Crea un cliente en la base de datos
     *
     * @param cliente
     * @throws SQLException
     * @throws InvalidDataException si no se pudo crear el cliente
     */
    public void crearCliente(Cliente cliente) throws SQLException, InvalidDataException {
        String statement = "INSERT INTO clientes (dpi,correo,estado,nombre,direccion,telefono,hobbies,descripcion) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, cliente.getIdentificador());
            st.setString(2, cliente.getCorreo());
            st.setString(3, EstadoEnum.ACTIVO.name());
            st.setString(4, cliente.getNombre());

            if (cliente.getDireccion() == null) {
                st.setNull(5, Types.VARCHAR);
            } else {
                st.setString(5, cliente.getDireccion());
            }

            if (cliente.getTelefono() == null) {
                st.setNull(6, Types.VARCHAR);
            } else {
                st.setString(6, cliente.getTelefono());
            }

            if (cliente.getHobbies() == null) {
                st.setNull(7, Types.VARCHAR);
            } else {
                st.setString(7, cliente.getHobbies());
            }

            if (cliente.getDescripcion() == null) {
                st.setNull(8, Types.VARCHAR);
            } else {
                st.setString(8, cliente.getDescripcion());
            }

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo crear el cliente");
            }
        }
    }

    /**
     * Actualiza un cliente en la base de datos
     *
     * @param cliente
     * @param identificadorOriginal
     * @throws SQLException
     * @throws InvalidDataException si no se pudo crear el cliente
     */
    public void actualizarCliente(Cliente cliente, String identificadorOriginal) throws SQLException, InvalidDataException {
        String statement = "UPDATE clientes SET dpi = ?, correo = ?, estado = ?, nombre = ?, direccion = ?, telefono = ?, hobbies = ?, descripcion = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, cliente.getIdentificador());
            st.setString(2, cliente.getCorreo());
            st.setString(3, cliente.getEstado().name());
            st.setString(4, cliente.getNombre());

            if (cliente.getDireccion() == null) {
                st.setNull(5, Types.VARCHAR);
            } else {
                st.setString(5, cliente.getDireccion());
            }

            if (cliente.getTelefono() == null) {
                st.setNull(6, Types.VARCHAR);
            } else {
                st.setString(6, cliente.getTelefono());
            }

            if (cliente.getHobbies() == null) {
                st.setNull(7, Types.VARCHAR);
            } else {
                st.setString(7, cliente.getHobbies());
            }

            if (cliente.getDescripcion() == null) {
                st.setNull(8, Types.VARCHAR);
            } else {
                st.setString(8, cliente.getDescripcion());
            }

            st.setString(9, identificadorOriginal);
            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo actualizar el cliente");
            }
        }
    }

    public boolean correoEstaEnUso(String correo) throws SQLException {
        String statement = "SELECT COUNT(*) as c FROM clientes WHERE correo = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, correo);

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    return result.getInt("c") != 0;
                }
                return false;
            }
        }
    }
}
