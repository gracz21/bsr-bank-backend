package pl.poznan.put.bsr.bank.providers;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * @author Kamil Walkowiak
 */
@Provider
public class CustomUnrecognizedPropertyExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<UnrecognizedPropertyException> {
    @Override
    public Response toResponse(UnrecognizedPropertyException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getPath().get(0).getFieldName() + " is unrecognized\"}")
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
