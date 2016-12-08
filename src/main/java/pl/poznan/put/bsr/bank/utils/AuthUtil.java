package pl.poznan.put.bsr.bank.utils;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.services.exceptions.BankServiceException;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
public abstract class AuthUtil {
    public static String getSessionIdFromHeaders(WebServiceContext context) throws BankServiceException {
        Map headers = (Map)context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList sessionId = (ArrayList)headers.get("Session-Id");
        if(sessionId != null) {
            return (String)sessionId.get(0);
        } else {
            throw new BankServiceException("Session id is missing");
        }
    }

    public static Session getSessionObject(String sessionId) throws BankServiceException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        Session session = datastore.find(Session.class).field("sessionId").equal(sessionId).get();
        if(session != null) {
            return session;
        } else {
            throw new BankServiceException("User is not logged in or session has expired");
        }
    }
}
