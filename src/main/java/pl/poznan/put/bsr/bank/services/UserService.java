package pl.poznan.put.bsr.bank.services;

import com.mongodb.DuplicateKeyException;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;
import pl.poznan.put.bsr.bank.utils.ValidateParamsUtil;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.util.*;

/**
 * Web Service class responsible for operations on users
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class UserService {
    @Resource
    private WebServiceContext context;

    /**
     * Creates new user
     * @param userName user login
     * @param password user password
     * @param firstName user first name
     * @param lastName user last name
     * @throws BankServiceException if given login is already used
     * @throws ValidationException if parameter validation fails
     */
    @WebMethod
    public void register(@WebParam(name = "userName") @XmlElement(required = true) String userName,
                         @WebParam(name = "password") @XmlElement(required = true) String password,
                         @WebParam(name = "firstName") @XmlElement(required = true) String firstName,
                         @WebParam(name = "lastName") @XmlElement(required = true) String lastName)
            throws BankServiceException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("user name", userName);
            put("password", password);
            put("first name", firstName);
            put("last name", lastName);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        try {
            datastore.save(new User(userName, password, firstName, lastName));
        } catch (DuplicateKeyException exception) {
            throw new BankServiceException("User name already used");
        }
    }

    /**
     * Creates session for given user
     * @param userName user login
     * @param password user password
     * @return created session id
     * @throws BankServiceException if wrong login/password has been given
     * @throws ValidationException if parameter validation fails
     */
    @WebMethod
    public String login(@WebParam(name = "userName") @XmlElement(required = true) String userName,
                        @WebParam(name = "password") @XmlElement(required = true) String password)
            throws BankServiceException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("user name", userName);
            put("password", password);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = datastore.find(User.class).field("userName").equal(userName).get();
        if (user == null) {
            throw new BankServiceException("Wrong user name");
        } else if (Arrays.equals(user.getPassword(), Base64.getEncoder().encode(password.getBytes()))) {
            Session session = new Session(UUID.randomUUID().toString(), user);
            datastore.save(session);
            return session.getSessionId();
        } else {
            throw new BankServiceException("Wrong password");
        }
    }

    /**
     * Logs out current user
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public void logout() throws AuthException {
        String sessionId = AuthUtil.getSessionIdFromWebServiceContext(context);
        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        Session session = AuthUtil.getSessionFromWebServiceContext(context, datastore);
        datastore.delete(session);
    }

    /**
     * Removes current user
     * @throws BankServiceException if current user already not exists
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public void deleteCurrentUser() throws BankServiceException, AuthException {
        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        Session session = AuthUtil.getSessionFromWebServiceContext(context, datastore);
        User user = session.getUser();

        datastore.delete(session);
        if (user != null) {
            for(BankAccount bankAccount: user.getBankAccounts()) {
                datastore.delete(bankAccount);
            }
            datastore.delete(user);
        } else {
            throw new BankServiceException("User already not exists");
        }
    }
}
