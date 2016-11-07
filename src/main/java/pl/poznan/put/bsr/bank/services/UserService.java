package pl.poznan.put.bsr.bank.services;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.services.messages.RegisterMessage;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class UserService {
    @WebMethod
    public void register(@WebParam RegisterMessage registerMessage) {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        datastore.save(new User(registerMessage.getUserName(), registerMessage.getPassword()));
    }
}
