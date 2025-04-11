package com.joca.salon.api.rest.backend.database;

import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.backend.exceptions.UserNotValidException;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.clientes.Cliente;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author joca
 */
public class DBUsuarios extends DBConnection {

    /**
     * Metodo que obtiene la contraseña de un usuario en base a su id
     *
     * @param identificador
     * @param T
     * @return credencialesDTO
     * @throws SQLException
     * @throws DataNotFoundException si no se encuentra ningun usuario
     * @throws com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException
     * @throws com.joca.salon.api.rest.backend.exceptions.UserNotValidException
     */
    public CredencialesDTO obtenerCredenciales(String identificador, Class T) throws SQLException, DataNotFoundException, PasswordNeedUpdateException, UserNotValidException {
        String statement = "SELECT contraseña FROM "+ T.getSimpleName().toLowerCase() + "s" + " WHERE " + obtenerIdentificadorLogin(T) + " = ? AND estado = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificador);
            st.setString(2, EstadoEnum.ACTIVO.name());

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    CredencialesDTO credenciales = new CredencialesDTO();
                    credenciales.setContraseña(result.getString("contraseña"));
                    credenciales.setIdentificador(identificador);

                    if (!credenciales.esValido()) {
                        throw new PasswordNeedUpdateException("La contraseña debe ser actualizada");
                    }
                    return credenciales;
                }
                throw new DataNotFoundException("No se encontraron credenciales para el usuario con identificador " + identificador);
            }
        }
    }
    
    /**
     * Verifica si un empleado existe en la base de datos
     *
     * @param identificador
     * @param T
     * @return true si existe
     * @throws SQLException
     */
    public boolean usuarioExiste(String identificador, Class T) throws SQLException {
        String statement = "SELECT COUNT(*) as c FROM "+ T.getSimpleName().toLowerCase() + "s" + " WHERE dpi = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificador);

            try (ResultSet result = st.executeQuery()) {
                if (result.next()) {
                    return result.getInt("c") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Busca un usuario en la base de datos y busca si esta inactivo
     *
     * @param identificador
     * @param T
     * @return true si esta inactivo
     * @throws SQLException
     */
    public boolean usuarioEstaInactivo(String identificador, Class T) throws SQLException {
        String statement = "SELECT estado FROM "+ T.getSimpleName().toLowerCase() + "s" + " WHERE dpi = ?";

        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, identificador);

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
     * Actualiza la contraseña de un empleado en base a su id
     *
     * @param credenciales
     * @param T
     * @throws SQLException
     * @throws InvalidDataException si el id no corresponde a ningun usuario
     */
    public void actualizarContraseña(CredencialesDTO credenciales, Class T) throws SQLException, InvalidDataException {
        String statement = "UPDATE "+ T.getSimpleName().toLowerCase() + "s" + " SET contraseña = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, credenciales.getContraseña());
            st.setString(2, credenciales.getIdentificador());

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se actualizo la contraseña del usuario");
            }
        }
    }
    
    /**
     * Pone en estado inactivo a un empleado
     *
     * @param identificadorUsuario
     * @param estado
     * @param T
     * @throws SQLException
     * @throws InvalidDataException si no se puede actualizar el empleado
     */
    public void editarEstadoUsuario(String identificadorUsuario, EstadoEnum estado, Class T) throws SQLException, InvalidDataException {
        String statement = "UPDATE "+ T.getSimpleName().toLowerCase() + "s" + " SET estado = ? WHERE dpi = ?";
        try (Connection conn = dbSingleton.getDatasource().getConnection(); PreparedStatement st = conn.prepareStatement(statement)) {
            st.setString(1, estado.name());
            st.setString(2, identificadorUsuario);

            int result = st.executeUpdate();
            if (result == 0) {
                throw new InvalidDataException("No se pudo desactivar el usuario");
            }
        }
    }
    
    /**
     * Devuelve el nombre de identificador para el tipo de usuario indicado
     * 
     * @param T
     * @return nombre del identificador
     * @throws UserNotValidException 
     */
    private String obtenerIdentificadorLogin(Class T) throws UserNotValidException {
        if (T.getCanonicalName().equals(Cliente.class.getCanonicalName())) {
            return "correo";
        }
        if (T.getCanonicalName().equals(Empleado.class.getCanonicalName())) {
            return "dpi";
        }
        throw new UserNotValidException("El tipo de usuario ingresado no es valido");
    }
    
}
