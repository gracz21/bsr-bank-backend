package pl.poznan.put.bsr.bank.providers;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Custom mapper class for unrecognized JSON property exception
 * @author Kamil Walkowiak
 */
@Provider
public class CustomUnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {
    /**
     * Parse exception to requested JSON format
     * @param e unrecognized JSON property exception
     * @return response HTTP 400 with explanation in JSON
     */
    @Override
    public Response toResponse(UnrecognizedPropertyException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getPath().get(0).getFieldName() + " is unrecognized\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
