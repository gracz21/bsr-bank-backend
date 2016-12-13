package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.handlers.SchemaValidationHandler;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;
import pl.poznan.put.bsr.bank.utils.SAXExceptionToValidationExceptionUtil;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@SchemaValidation(handler = SchemaValidationHandler.class)
public class BankAccountService {
    @Resource
    private WebServiceContext context;

    @WebMethod
    public List<BankAccount> getCurrentUserBankAccounts() throws BankServiceException, AuthException {
        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        return user.getBankAccounts();
    }

    @WebMethod
    public void addBankAccount(@WebParam(name = "name") @XmlElement(required = true) String name)
            throws BankServiceException, AuthException, ValidationException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = new BankAccount(name);
        user.addBankAccount(bankAccount);
        datastore.save(bankAccount);
        datastore.save(user);
    }

    @WebMethod
    public void deleteBankAccount(@WebParam(name = "accountNo") @XmlElement(required = true) String accountNo)
            throws BankServiceException, AuthException, ValidationException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();;
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(accountNo).get();
        if(bankAccount == null) {
            throw new BankServiceException("Bank account with given no not exists");
        }
        if(user.containsBankAccount(bankAccount.getAccountNo())) {
            user.removeBankAccount(bankAccount.getAccountNo());
            datastore.delete(bankAccount);
            datastore.save(user);
        } else {
            throw new BankServiceException("User does not own this bank account");
        }
    }
}
