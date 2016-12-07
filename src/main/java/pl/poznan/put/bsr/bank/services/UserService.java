package pl.poznan.put.bsr.bank.services;

import com.mongodb.DuplicateKeyException;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.services.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.awt.*;
import java.util.*;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class UserService {
    @Resource
    private WebServiceContext context;

    @WebMethod
    public void register(@WebParam(name = "userName") @XmlElement(required=true) String userName,
                         @WebParam(name = "password") @XmlElement(required=true) String password,
                         @WebParam(name = "firstName") @XmlElement(required=true) String firstName,
                         @WebParam(name = "lastName") @XmlElement(required=true) String lastName)
            throws BankServiceException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        try {
            datastore.save(new User(userName, password, firstName, lastName));
        } catch(DuplicateKeyException exception) {
            throw new BankServiceException("User name already used");
        }
    }

    @WebMethod
    public String login(@WebParam(name = "userName") @XmlElement(required=true) String userName,
                      @WebParam(name = "password") @XmlElement(required=true) String password)
            throws BankServiceException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        User user = datastore.find(User.class).field("userName").equal(userName).get();
        if(user == null) {
            throw new BankServiceException("Wrong user name");
        } else if(Arrays.equals(user.getPassword(), Base64.getEncoder().encode(password.getBytes()))) {
            Session session = new Session(UUID.randomUUID().toString(), user);
            datastore.save(session);
            return session.getSessionId();
        } else {
            throw new BankServiceException("Wrong password");
        }
    }

    @WebMethod
    public void logout() throws BankServiceException {
        String sessionId = getSessionIdFromHeaders();
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        Session session = datastore.find(Session.class).field("sessionId").equal(sessionId).get();
        if(session != null) {
            datastore.delete(session);
        } else {
            throw new BankServiceException("User is not logged in or session has expired");
        }
    }

    @WebMethod
    public void deleteCurrentUser() throws BankServiceException {
        String sessionId = getSessionIdFromHeaders();
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        Session session = datastore.find(Session.class).field("sessionId").equal(sessionId).get();
        if(session != null) {
            User user = session.getUser();
            datastore.delete(session);
            if(user != null) {
                datastore.delete(user);
            } else {
                throw new BankServiceException("User already not exists");
            }
        } else {
            throw new BankServiceException("User is not logged in or session has expired");
        }
    }

    private String getSessionIdFromHeaders() throws BankServiceException {
        Map headers = (Map)context.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
        ArrayList sessionId = (ArrayList)headers.get("Session-Id");
        if(sessionId != null) {
            return (String)sessionId.get(0);
        } else {
            throw new BankServiceException("Session id is missing");
        }
    }
}
