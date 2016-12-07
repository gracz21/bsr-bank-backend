package pl.poznan.put.bsr.bank.services;

import com.mongodb.DuplicateKeyException;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.services.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class UserService {
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
            return UUID.randomUUID().toString();
        } else {
            throw new BankServiceException("Wrong password");
        }
    }
}
