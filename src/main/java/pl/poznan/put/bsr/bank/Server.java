package pl.poznan.put.bsr.bank;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.jaxws.JaxwsHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import pl.poznan.put.bsr.bank.service.ExampleService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * @author Kamil Walkowiak
 */
public class Server {
    public static void main(String[] args) throws IOException {
        //Initialize REST
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig().packages("rest");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);

        //Initialize WS SOAP
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", 8080);
        server.getServerConfiguration().addHttpHandler(new JaxwsHandler(new ExampleService()), "/add");
        server.addListener(networkListener);

        server.start();
    }
}
