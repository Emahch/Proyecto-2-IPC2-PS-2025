package com.joca.salon.api.rest.resources;

import com.joca.salon.api.rest.backend.exceptions.DataNotFoundException;
import com.joca.salon.api.rest.backend.exceptions.DuplicatedDataException;
import com.joca.salon.api.rest.backend.exceptions.InvalidDataException;
import com.joca.salon.api.rest.backend.exceptions.PasswordNeedUpdateException;
import com.joca.salon.api.rest.backend.exceptions.UserNotValidException;
import com.joca.salon.api.rest.backend.services.ClientesService;
import com.joca.salon.api.rest.backend.services.UsuariosService;
import com.joca.salon.api.rest.model.EstadoEnum;
import com.joca.salon.api.rest.model.usuarios.CredencialesDTO;
import com.joca.salon.api.rest.model.usuarios.clientes.Cliente;
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
@Path("cliente")
public class ClientesResource {

    /**
     * Realiza el intento de un login para un cliente
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
        UsuariosService usuariosService = new UsuariosService();
        try {
            String token = usuariosService.iniciarSesion(credenciales, Cliente.class);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return Response.ok(response).build();
        } catch (DataNotFoundException | InvalidDataException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("El correo o contraseña son incorrectos").build();
        } catch (SQLException | UserNotValidException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (PasswordNeedUpdateException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(credenciales.getIdentificador()).build();
        }
    }
    
    /**
     * Registra un nuevo cliente en el sistema
     *
     * @param cliente
     * @return status code: 406 si los datos del cliente no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * 201 si se realiza el registro de forma exitosa
     */
    @POST
    @Path("registro")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registro(Cliente cliente) {
        ClientesService clientesService = new ClientesService();
        try {
            clientesService.registrarCliente(cliente);
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
     * Desactiva un cliente en el sistema
     *
     * @param identificador
     * @return status code: 406 si los datos del cliente no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("desactivar/{identificador}")
    @RolesAllowed("CLIENTE")
    public Response desactivarCliente(@PathParam("identificador") String identificador) {
        UsuariosService usuariosService = new UsuariosService();
        try {
            usuariosService.editarEstadoUsuario(identificador, EstadoEnum.INACTIVO, Cliente.class);
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
     * Desactiva un cliente en el sistema
     *
     * @param identificador
     * @return status code: 406 si los datos del cliente no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("blacklist/{identificador}")
    public Response ponerClienteEnListaNegra(@PathParam("identificador") String identificador) {
        UsuariosService usuariosService = new UsuariosService();
        try {
            usuariosService.editarEstadoUsuario(identificador, EstadoEnum.LISTA_NEGRA, Cliente.class);
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
     * Edita la información de un cliente en el sistema
     *
     * @param identificador
     * @param cliente
     * @return status code: 406 si los datos del cliente no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("actualizar/{identificador}")
    @RolesAllowed("CLIENTE")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarCliente(@PathParam("identificador") String identificador, Cliente cliente) {
        ClientesService clientesService = new ClientesService();
        try {
            clientesService.actualizarCliente(cliente, identificador);
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
     * Edita la contraseña de un cliente en el sistema
     *
     * @param credenciales
     * @return status code: 406 si los datos ingresados no son validos,
     * 500 si ocurre un error al realizar el proceso.
     * ok si se realiza el proceso de forma exitosa
     */
    @POST
    @Path("password")
    @RolesAllowed("CLIENTE")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarContraseñaCliente(CredencialesDTO credenciales) {
        UsuariosService usuariosService = new UsuariosService();
        try {
            usuariosService.actualizarContraseña(credenciales, Cliente.class);
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
     * Obtiene la informacion de un cliente en base a su identificador
     *
     * @param identificador
     * @return status code: 404 si no se encuentra ningun cliente,
     * 500 si ocurre un error al realizar el proceso.
     * Cliente si se encuentra el cliente
     */
    @GET
    @Path("{identificador}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCliente(@PathParam("identificador") String identificador) {
        ClientesService clientesService = new ClientesService();
        try {
            Cliente cliente = clientesService.obtenerCliente(identificador, "dpi");
            return Response.ok(cliente).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (DataNotFoundException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    /**
     * Obtiene la informacion de todos los clientes en el sistema
     *
     * @return status code: 500 si ocurre un error al realizar el proceso.
     * Clientes encontrados
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerClientes() {
        ClientesService clientesService = new ClientesService();
        try {
            List<Cliente> clientes = clientesService.obtenerClientes();
            return Response.ok(clientes).build();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
