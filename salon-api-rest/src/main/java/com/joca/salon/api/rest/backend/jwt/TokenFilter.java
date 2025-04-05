package com.joca.salon.api.rest.backend.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 *
 * @author emahch
 */
@Provider
@jakarta.annotation.Priority(Priorities.AUTHORIZATION)
public class TokenFilter implements ContainerRequestFilter {

    private JwtManager jwtManager;

    @Inject
    private ResourceInfo resourceInfo;

    public TokenFilter() {
        this.jwtManager = new JwtManager();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        if (path.startsWith("empleado/login") || path.startsWith("cliente/login")) {
            return;
        }
        String authorizationHeader = requestContext.getHeaderString("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "No se encontro un header de autorización");
            return;
        }

        String token = authorizationHeader.substring(7); //Omitiendo "Bearer "
        try {
            DecodedJWT decodedJWT = jwtManager.getVerifier().verify(token);

            Date expirationDate = decodedJWT.getExpiresAt();
            if (expirationDate != null && expirationDate.before(new Date())) {
                abortWithUnauthorized(requestContext, "El token ha expirado");
                return;
            }

            String role = decodedJWT.getClaim("rol").asString();
            // Validar si existe el rol en @RolesAllowed
            if (!isUserInRole(role)) {
                abortWithForbidden(requestContext, "El usuario no esta autorizado para acceder a esta funcionalidad");
            }

        } catch (JWTVerificationException e) {
            abortWithUnauthorized(requestContext, "Token Invalido");
        }
    }

    private boolean isUserInRole(String roleFromToken) {
        // Obtener la clase o método solicitado
        Method method = resourceInfo.getResourceMethod();

        // Verificar si @RolesAllowed está presente en el método
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            String[] allowedRoles = method.getAnnotation(RolesAllowed.class).value();
            for (String role : allowedRoles) {
                if (roleFromToken.equals(role)) {
                    return true;  // El usuario tiene uno de los roles permitidos
                }
            }
        }

        // Si no está en el método, verificar si @RolesAllowed está en la clase
        if (resourceInfo.getResourceClass().isAnnotationPresent(RolesAllowed.class)) {
            String[] allowedRoles = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class).value();
            for (String role : allowedRoles) {
                if (roleFromToken.equals(role)) {
                    return true;
                }
            }
        }
        
        if (method.isAnnotationPresent(PermitAll.class)) { 
            return true;
        }

        // Si no tiene ninguno de los roles permitidos
        return false;
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(message)
                        .build()
        );
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(message)
                        .build()
        );
    }
}
