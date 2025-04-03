package com.joca.salon.api.rest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.time.LocalTime;

/**
 *
 * @author 
 */
@Path("test")
public class JakartaEE10Resource {
    
    @GET
    public Response ping(){
        LocalTime time = LocalTime.of(1, 24);
        return Response
                .ok(String.valueOf(time.getHour()) + ":" + String.valueOf(time.getMinute()))
                .build();
    }
}
