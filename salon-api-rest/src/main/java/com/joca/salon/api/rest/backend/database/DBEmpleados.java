package com.joca.salon.api.rest.backend.database;

import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import com.joca.salon.api.rest.model.usuarios.empleados.RolEnum;
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
public class DBEmpleados extends DBConnection {

    /**
     * Metodo que obtiene la contraseña de un empleado en base a su id
     *
     * @param identificadorEmpleado
     * @return credencialesDTO
     * @throws SQLException
     * @throws DataNotFoundException si no se encuentra ningun empleado
     */
    public CredencialesDTO obtenerCredenciales(String identificadorEmpleado) throws SQLException, DataNotFoundException, PasswordNeedUpdateException {
        String statement = "SELECT contraseña FROM empleados WHERE dpi = ? AND estado = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificadorEmpleado);
            st.setString(2, EstadoEnum.ACTIVO.name());

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    CredencialesDTO credenciales = new CredencialesDTO();
                    credenciales.setContraseña(result.getString("contraseña"));
                    credenciales.setIdentificador(identificadorEmpleado);

                    if (!credenciales.esValido()) {
                        throw new PasswordNeedUpdateException("La contraseña debe ser actualizada");
                    }
                    return credenciales;
                }
                throw new DataNotFoundException("No se encontraron credenciales para el identificador " + identificadorEmpleado);
            }
        }
    }

    /**
     * Obtiene un empleado en base a su identificador
     *
     * @param identificadorEmpleado
     * @return empleado
     * @throws SQLException
     * @throws DataNotFoundException si no se encuentra ningun empleado
     */
    public Empleado obtenerEmpleado(String identificadorEmpleado) throws SQLException, DataNotFoundException {
        String statement = "SELECT * FROM empleados WHERE dpi = ? AND estado = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificadorEmpleado);
            st.setString(2, EstadoEnum.ACTIVO.name());

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    Empleado empleado = new Empleado();
                    empleado.setIdentificador(result.getString("dpi"));
                    empleado.setNombre(result.getString("nombre"));
                    empleado.setDescripcion(result.getString("descripcion"));
                    empleado.setRol(RolEnum.valueOf(result.getString("rol")));
                    empleado.setFotografia(result.getBytes("fotografia"));
                    return empleado;
                }
                throw new DataNotFoundException("No se encontro el empleado, verifica que la información sea correcta");
            }
        }
    }

    /**
     * Obtiene todos los empleados activos en la base de datos
     *
     * @return Lista Empleados
     * @throws SQLException
     */
    public List<Empleado> obtenerEmpleados() throws SQLException {
        String statement = "SELECT * FROM empleados WHERE estado = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, EstadoEnum.ACTIVO.name());

            try (ResultSet result = st.executeQuery()) {
                List<Empleado> empleados = new ArrayList<>();
                while (result.next()) {
                    Empleado empleado = new Empleado();
                    empleado.setIdentificador(result.getString("dpi"));
                    empleado.setNombre(result.getString("nombre"));
                    empleado.setDescripcion(result.getString("descripcion"));
                    empleado.setRol(RolEnum.valueOf(result.getString("rol")));
                    empleado.setFotografia(result.getBytes("fotografia"));
                    empleados.add(empleado);
                }
                return empleados;
            }
        }
    }

    /**
     * Verifica si un empleado existe en la base de datos
     *
     * @param identificadorEmpleado
     * @return true si existe
     * @throws SQLException
     */
    public boolean empleadoExiste(String identificadorEmpleado) throws SQLException {
        String statement = "SELECT COUNT(*) as c FROM empleados WHERE dpi = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificadorEmpleado);

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    return result.getInt("c") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Busca un empleado en la base de datos y busca si esta inactivo
     *
     * @param identificadorEmpleado
     * @return true si esta inactivo
     * @throws SQLException
     */
    public boolean empleadoEstaInactivo(String identificadorEmpleado) throws SQLException {
        String statement = "SELECT estado FROM empleados WHERE dpi = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificadorEmpleado);

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    String estado = result.getString("estado");
                    return EstadoEnum.valueOf(estado).equals(EstadoEnum.INACTIVO);
                }
            }
        }
        return false;
    }

    /**
     * Crea un empleado en la base de datos
     *
     * @param empleado
     * @throws SQLException
     * @throws InvalidDataException si no se pudo crear el empleado
     */
    public void crearEmpleado(Empleado empleado) throws SQLException, InvalidDataException {
        String statement = "INSERT INTO empleados (dpi,nombre,descripcion,estado,rol,fotografia) VALUES (?,?,?,?,?,?)";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, empleado.getIdentificador());
            st.setString(2, empleado.getNombre());

            if (empleado.getDescripcion() == null) {
                st.setNull(3, Types.VARCHAR);
            } else {
                st.setString(3, empleado.getDescripcion());
            }

            st.setString(4, EstadoEnum.ACTIVO.name());
            st.setString(5, empleado.getRol().name());

            if (empleado.getFotografia() == null) {
                st.setNull(6, Types.BLOB);
            } else {
                st.setBytes(6, empleado.getFotografia());
            }

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo crear el empleado");
            }
        }
    }

    /**
     * Actualiza un empleado en la base de datos
     *
     * @param empleado
     * @param identificadorOriginal
     * @throws SQLException
     * @throws InvalidDataException si no se pudo crear el empleado
     */
    public void actualizarEmpleado(Empleado empleado, String identificadorOriginal) throws SQLException, InvalidDataException {
        String statement = "UPDATE empleados SET dpi = ?, nombre = ?, descripcion = ?, estado = ? , rol = ?, fotografia = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, empleado.getIdentificador());
            st.setString(2, empleado.getNombre());

            if (empleado.getDescripcion() == null) {
                st.setNull(3, Types.VARCHAR);
            } else {
                st.setString(3, empleado.getDescripcion());
            }

            st.setString(4, EstadoEnum.ACTIVO.name());
            st.setString(5, empleado.getRol().name());

            if (empleado.getFotografia() == null) {
                st.setNull(6, Types.BLOB);
            } else {
                st.setBytes(6, empleado.getFotografia());
            }

            st.setString(7, identificadorOriginal);
            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo actualizar el empleado");
            }
        }
    }

    /**
     * Actualiza la contraseña de un empleado en base a su id
     *
     * @param credenciales
     * @throws SQLException
     * @throws InvalidDataException si el id no corresponde a ningun usuario
     */
    public void actualizarContraseña(CredencialesDTO credenciales) throws SQLException, InvalidDataException {
        String statement = "UPDATE empleados SET contraseña = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, credenciales.getContraseña());
            st.setString(2, credenciales.getIdentificador());

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se actualizo la contraseña del empleado");
            }
        }
    }

    /**
     * Pone en estado inactivo a un empleado
     *
     * @param identificadorEmpleado
     * @throws SQLException
     * @throws InvalidDataException si no se puede actualizar el empleado
     */
    public void desactivarEmpleado(String identificadorEmpleado) throws SQLException, InvalidDataException {
        String statement = "UPDATE empleados SET estado = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, EstadoEnum.INACTIVO.name());
            st.setString(2, identificadorEmpleado);

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo desactivar el empleado");
            }
        }
    }
}
