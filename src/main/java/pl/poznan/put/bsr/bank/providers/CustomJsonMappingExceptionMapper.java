package pl.poznan.put.bsr.bank.providers;

import com.fasterxml.jackson.databind.JsonMappingException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Custom mapper class for JSON on fields mapping exceptions
 * @author Kamil Walkowiak
 */
@Provider
public class CustomJsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    /**
     * Parse exception to requested JSON format
     * @param e JSON mapping exception
     * @return response HTTP 400 with explanation in JSON
     */
    @Override
    public Response toResponse(JsonMappingException e) {
        String message;
        if (e.getPath().size() != 0) {
            message = "{\"error\":\"" + e.getPath().get(0).getFieldName() + " is invalid\"}";
        } else {
            message = "{\"error\": \"invalid JSON format\"}";
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
