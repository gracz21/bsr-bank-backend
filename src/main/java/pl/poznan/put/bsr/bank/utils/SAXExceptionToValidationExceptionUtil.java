package pl.poznan.put.bsr.bank.utils;


import org.xml.sax.SAXParseException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.handlers.SchemaValidationHandler;

import javax.xml.ws.handler.MessageContext;

/**
 * @author Kamil Walkowiak
 */
public abstract class SAXExceptionToValidationExceptionUtil {
    public static void parseExceptions(MessageContext context) throws ValidationException {
        SAXParseException errorException = (SAXParseException)context.get(SchemaValidationHandler.ERROR);

        String errorMessage = null;

        if (errorException != null) {
            errorMessage = errorException.getMessage();
        }

        if(errorMessage != null) {
            String fieldName = errorMessage.substring(errorMessage.indexOf("{") + 1, errorMessage.indexOf("}"));
            throw new ValidationException(fieldName + " is missing");
        }
    }
}
