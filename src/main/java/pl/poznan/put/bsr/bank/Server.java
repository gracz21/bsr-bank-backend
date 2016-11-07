package pl.poznan.put.bsr.bank;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.services.ExampleService;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * @author Kamil Walkowiak
 */
public class Server {
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        DataStoreHandlerUtil.getInstance().initializeDataStore();
        initializeRESTServer();
        initializeSOAPServer();
        server.start();
    }

    private static void initializeRESTServer() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig().packages("rest");
        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);
    }

    private static void initializeSOAPServer() {
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", 8080);
        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new ExampleService()), "/add");
        server.addListener(networkListener);
    }
}