package pl.poznan.put.bsr.bank;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import pl.poznan.put.bsr.bank.providers.CustomJsonMappingExceptionMapper;
import pl.poznan.put.bsr.bank.providers.CustomJsonProcessingExceptionMapper;
import pl.poznan.put.bsr.bank.providers.CustomUnrecognizedPropertyExceptionMapper;
import pl.poznan.put.bsr.bank.providers.EmptyRequestsFilter;
import pl.poznan.put.bsr.bank.utils.BasicAuthFilterUtil;

/**
 * @author Kamil Walkowiak
 */
class CustomResourceConfig extends ResourceConfig {
    CustomResourceConfig() {
        packages("pl.poznan.put.bsr.bank.resources");
        register(JacksonJaxbJsonProvider.class);
        register(BasicAuthFilterUtil.class);
        register(CustomUnrecognizedPropertyExceptionMapper.class);
        register(CustomJsonMappingExceptionMapper.class);
        register(CustomJsonProcessingExceptionMapper.class);
        register(EmptyRequestsFilter.class);
    }
}
