package com.joca.salon.api.rest.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.joca.salon.api.rest.model.usuarios.Usuario;
import com.joca.salon.api.rest.model.usuarios.empleados.Empleado;
import com.joca.salon.api.rest.model.usuarios.empleados.RolEnum;
import java.util.Date;

/**
 *
 * @author emahch
 */
public class JwtManager {

    private final String KEY = "ALKSJQCNOA142PJANYASXMPLLNQWDESAS18310";
    private final String ISSUER = "salon-api-rest";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; //24 horas

    /**
     * Genera un token en base a un usuario ya autenticado
     *
     * @param usuario
     * @return token | null si ocurre un error
     */
    public String generateToken(Usuario usuario) {
        String rol = RolEnum.CLIENTE.name();
        if (usuario instanceof Empleado) {
            Empleado empleado = (Empleado) usuario;
            rol = empleado.getRol().name();
        }
        return JWT.create()
                .withSubject(usuario.getIdentificador())
                .withClaim("rol", rol)
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * Devuelve el verificador para los tokens
     *
     * @return verificador de token
     */
    public JWTVerifier getVerifier() {
        return JWT.require(Algorithm.HMAC256(KEY)) // Clave secreta
                .withIssuer(ISSUER)
                .build();
    }
}
