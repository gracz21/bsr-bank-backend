package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.handlers.SchemaValidationHandler;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;
import pl.poznan.put.bsr.bank.utils.SAXExceptionToValidationExceptionUtil;

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
@SchemaValidation(handler = SchemaValidationHandler.class)
public class BankAccountService {
    @Resource
    private WebServiceContext context;

    @WebMethod
    public List<BankAccount> getCurrentUserBankAccounts() throws BankServiceException, AuthException {
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
    public void addBankAccount(@WebParam(name = "name") @XmlElement(required = true) String name)
            throws BankServiceException, AuthException, ValidationException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        String sessionId = AuthUtil.getSessionIdFromHeaders(context);
        Session session = AuthUtil.getSessionObject(sessionId);
        User user = session.getUser();

        if(user != null) {
            BankAccount bankAccount = new BankAccount(name);
            user.addBankAccount(bankAccount);
            datastore.save(bankAccount);
            datastore.save(user);
        } else {
            datastore.delete(session);
            throw new BankServiceException("User assigned to this session not exists");
        }
    }

    @WebMethod
    public void deleteBankAccount(@WebParam(name = "accountNo") @XmlElement(required = true) String accountNo)
            throws BankServiceException, AuthException, ValidationException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        String sessionId = AuthUtil.getSessionIdFromHeaders(context);
        Session session = AuthUtil.getSessionObject(sessionId);
        User user = session.getUser();

        if(user != null) {
            BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(accountNo).get();
            if(bankAccount == null) {
                throw new BankServiceException("Bank account with given no not exists");
            }
            if(user.containsBankAccount(bankAccount.getAccountNo())) {
                user.removeBankAccount(bankAccount.getAccountNo());
                datastore.delete(bankAccount);
                datastore.save(user);
            } else {
                throw new BankServiceException("You do not own this bank account");
            }
        } else {
            datastore.delete(session);
            throw new BankServiceException("User assigned to this session not exists");
        }
    }
}
