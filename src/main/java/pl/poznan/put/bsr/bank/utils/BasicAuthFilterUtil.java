package pl.poznan.put.bsr.bank.utils;

import org.glassfish.jersey.internal.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
public class BasicAuthFilterUtil implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String auth = containerRequestContext.getHeaderString("Authorization");
        if(auth == null){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String[] credentials = Base64.decodeAsString(auth).split(":");
        if(credentials.length < 2 || !credentials[1].equals(ConstantsUtil.BANK_PASSWORD)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Map<String, String> bankToIpMap = MapBankToIpUtil.getInstance().getBankToIpMap();
        if(!bankToIpMap.containsKey(credentials[0])) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }
}
