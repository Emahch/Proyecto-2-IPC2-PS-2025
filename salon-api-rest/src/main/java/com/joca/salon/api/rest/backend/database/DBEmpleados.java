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
                    empleados.add(empleado);
                }
                return empleados;
            }
        }
    }

    /**
     * Crea un empleado en la base de datos
     *
     * @param empleado
     * @throws SQLException
     * @throws InvalidDataException si no se pudo crear el empleado
     */
    public void crearEmpleado(Empleado empleado) throws SQLException, InvalidDataException {
        String statement = "INSERT INTO empleados (dpi,nombre,descripcion,estado,rol) VALUES (?,?,?,?,?)";
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
        String statement = "UPDATE empleados SET dpi = ?, nombre = ?, descripcion = ?, estado = ? , rol = ? WHERE dpi = ?";
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
            st.setString(6, identificadorOriginal);
            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo actualizar el empleado");
            }
        }
    }
}
