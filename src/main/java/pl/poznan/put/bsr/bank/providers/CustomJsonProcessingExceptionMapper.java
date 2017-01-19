package pl.poznan.put.bsr.bank.providers;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Custom mapper class for JSON processing exceptions
 * @author Kamil Walkowiak
 */
@Provider
public class CustomJsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    /**
     * Parse exception to requested JSON format
     * @param e JSON processing exception
     * @return response HTTP 400 with explanation in JSON
     */
    @Override
    public Response toResponse(JsonProcessingException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"invalid JSON format\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
