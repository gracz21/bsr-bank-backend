package pl.poznan.put.bsr.bank.utils;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
public abstract class AuthUtil {
    public static String getSessionIdFromWebServiceContext(WebServiceContext context) throws AuthException {
        Map headers = (Map)context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList sessionId = (ArrayList)headers.get("Session-Id");
        if(sessionId == null) {
            throw new AuthException("Session id is missing");
        }

        return (String)sessionId.get(0);
    }

    public static Session getSessionFromWebServiceContext(WebServiceContext context, Datastore datastore) throws AuthException {
        String sessionId = getSessionIdFromWebServiceContext(context);
        Session session = datastore.find(Session.class).field("sessionId").equal(sessionId).get();
        if(session == null) {
            throw new AuthException("User is not logged in or session has expired");
        }

        return session;
    }

    public static User getUserFromWebServiceContext(WebServiceContext context, Datastore datastore) throws AuthException {
        Session session = getSessionFromWebServiceContext(context, datastore);
        User user = session.getUser();
        if(user == null) {
            datastore.delete(session);
            throw new AuthException("User assigned to this session not exists");
        }

        return user;
    }
}
