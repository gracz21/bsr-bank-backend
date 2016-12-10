package pl.poznan.put.bsr.bank.handlers;

import com.sun.xml.ws.developer.ValidationErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Kamil Walkowiak
 */
public class SchemaValidationHandler extends ValidationErrorHandler {
    public static final String WARNING = "SchemaValidationWarning";
    public static final String ERROR = "SchemaValidationError";
    public static final String FATAL_ERROR = "SchemaValidationFatalError";

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(WARNING, exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(ERROR, exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(FATAL_ERROR, exception);
    }
}
