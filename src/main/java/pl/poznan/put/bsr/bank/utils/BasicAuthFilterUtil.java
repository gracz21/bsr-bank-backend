package pl.poznan.put.bsr.bank.utils;

import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Kamil Walkowiak
 */
public class BasicAuthFilterUtil implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String auth = containerRequestContext.getHeaderString("Authorization");
        if (auth == null) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"missing bank authentication data\"}").build());
        }

        String[] credentials = Base64.decodeAsString(auth.substring(6)).split(":");
        if (credentials.length < 2) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"missing bank login or password\"}").build());
        }
        if (!credentials[1].equals(ConstantsUtil.BANK_PASSWORD)) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"wrong bank password\"}").build());
        }
        if (!credentials[0].equals(ConstantsUtil.BANK_USERNAME)) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"wrong bank login\"}").build());
        }
    }
}
