package pl.poznan.put.bsr.bank;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import pl.poznan.put.bsr.bank.services.BankAccountService;
import pl.poznan.put.bsr.bank.services.BankOperationService;
import pl.poznan.put.bsr.bank.services.UserService;
import pl.poznan.put.bsr.bank.utils.BasicAuthFilterUtil;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;
import pl.poznan.put.bsr.bank.utils.MapBankToIpUtil;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SimpleTimeZone;

/**
 * @author Kamil Walkowiak
 */
public class Server {
    private static HttpServer server;

    public static void main(String[] args) throws IOException, URISyntaxException {
        DataStoreHandlerUtil.getInstance().initializeDataStore();
        initializeRESTServer();
        initializeSOAPServer();
        server.start();
    }

    private static void initializeRESTServer() throws IOException, URISyntaxException {
        MapBankToIpUtil.getInstance().initialize();
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(ConstantsUtil.REST_PORT).build();
        ResourceConfig config = new ResourceConfig().packages("pl.poznan.put.bsr.bank.resources");
        config.register(BasicAuthFilterUtil.class);
        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);
    }

    private static void initializeSOAPServer() {
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", ConstantsUtil.SOAP_PORT);
        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new UserService()), "/users");
        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new BankAccountService()), "/bankAccounts");
        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new BankOperationService()), "/bankOperations");
        server.addListener(networkListener);
    }
}
