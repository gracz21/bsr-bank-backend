package pl.poznan.put.bsr.bank.services;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.services.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.WebServiceContext;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class BankAccountService {
    @Resource
    private WebServiceContext context;

    @WebMethod
    public List<BankAccount> getCurrentUserBankAccounts() throws BankServiceException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        String sessionId = AuthUtil.getSessionIdFromHeaders(context);
        Session session = AuthUtil.getSessionObject(sessionId);
        User user = session.getUser();

        if(user != null) {
            return user.getBankAccounts();
        } else {
            datastore.delete(session);
            throw new BankServiceException("User assigned to this session not exists");
        }
    }

    @WebMethod
    public void addBankAccount(@WebParam(name = "name") @XmlElement(required=true) String name)
            throws BankServiceException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        String sessionId = AuthUtil.getSessionIdFromHeaders(context);
        Session session = AuthUtil.getSessionObject(sessionId);
        User user = session.getUser();

        if(user != null) {
            BankAccount bankAccount = new BankAccount(name);
            user.addAccount(bankAccount);
            datastore.save(bankAccount);
            datastore.save(user);
        } else {
            datastore.delete(session);
            throw new BankServiceException("User assigned to this session not exists");
        }
    }
}
