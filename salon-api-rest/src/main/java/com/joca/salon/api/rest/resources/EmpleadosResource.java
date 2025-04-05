package com.joca.salon.api.rest.resources;

import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.backend.services.EmpleadosService;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author
 */
@Path("empleado")
public class EmpleadosResource {

    /**
     * Realiza el intento de un login para un empleado
     *
     * @param credenciales
     * @return status code: 406 si el identificador o contraseña son
     * incorrectos, 500 si ocurre un error al realizar el proceso. JWT token
     * correspondiente si se realiza el login de forma exitosa
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response iniciarSesion(CredencialesDTO credenciales) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            String token = empleadosService.iniciarSesion(credenciales);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return Response.ok(response).build();
        } catch (DataNotFoundException | InvalidDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("El identificador o contraseña son incorrectos").build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (PasswordNeedUpdateException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(credenciales.getIdentificador()).build();
        }
    }
    
    /**
     * Registra un nuevo empleado en el sistema
     *
     * @param empleado
     * @return status code: 406 si los datos del empleado no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * 201 si se realiza el registro de forma exitosa
     */
    @POST
    @RolesAllowed("ADMINISTRADOR")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registro(Empleado empleado) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            empleadosService.registrarEmpleado(empleado);
            return Response.status(Response.Status.CREATED).build();
        } catch (InvalidDataException | DuplicatedDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ex.getMessage()).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Desactiva un empleado en el sistema
     *
     * @param identificador
     * @return status code: 406 si los datos del empleado no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("desactivar/{identificador}")
    @RolesAllowed("ADMINISTRADOR")
    public Response desactivarEmpleado(@PathParam("identificador") String identificador) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            empleadosService.desactivarEmpleado(identificador);
            return Response.ok().build();
        } catch (InvalidDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ex.getMessage()).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Edita la información de un empleado en el sistema
     *
     * @param identificador
     * @param empleado
     * @return status code: 406 si los datos del empleado no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("actualizar/{identificador}")
    @RolesAllowed({"ADMINISTRADOR","EMPLEADO","MARKETING","SERVICIOS"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarEmpleado(@PathParam("identificador") String identificador, Empleado empleado) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            empleadosService.actualizarEmpleado(empleado, identificador);
            return Response.ok().build();
        } catch (InvalidDataException | DuplicatedDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ex.getMessage()).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Edita la contraseña de un empleado en el sistema
     *
     * @param credenciales
     * @return status code: 406 si los datos ingresados no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("password")
    @RolesAllowed({"ADMINISTRADOR","EMPLEADO","MARKETING","SERVICIOS"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarContraseñaEmpleado(CredencialesDTO credenciales) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            empleadosService.actualizarContraseña(credenciales);
            return Response.ok().build();
        } catch (InvalidDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(ex.getMessage()).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene la informacion de un empleado en base a su identificador
     *
     * @param identificador
     * @return status code: 404 si no se encuentra ningun empleado,
     * 500 si ocurre un error al realizar el proceso.
     * Empleado si se encuentra el empleado
     */
    @GET
    @Path("{identificador}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerEmpleado(@PathParam("identificador") String identificador) {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            Empleado empleado = empleadosService.obtenerEmpleado(identificador);
            return Response.ok(empleado).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Obtiene la informacion de un empleado en base a su identificador
     *
     * @return status code: 500 si ocurre un error al realizar el proceso.
     * Empleados encontrados
     */
    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerEmpleado() {
        EmpleadosService empleadosService = new EmpleadosService();
        try {
            List<Empleado> empleados = empleadosService.obtenerEmpleados();
            return Response.ok(empleados).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
