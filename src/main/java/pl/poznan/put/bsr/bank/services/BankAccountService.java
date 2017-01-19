package pl.poznan.put.bsr.bank.services;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.models.BankAccount;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service class responsible for operations connected with bank accounts
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class BankAccountService {
    @Resource
    private WebServiceContext context;

    /**
     * Retrieves bank accounts (with operations history) of current logged in user
     * @return list of bank accounts of current user
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public List<BankAccount> getCurrentUserBankAccounts() throws AuthException {
        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        return user.getBankAccounts();
    }

    /**
     * Creates new bank account for current logged in user
     * @param name name of new bank account
     * @return created bank account object
     * @throws AuthException if authorization process fails
     * @throws ValidationException if parameter validation fails
     */
    @WebMethod
    public BankAccount addBankAccount(@WebParam(name = "name") @XmlElement(required = true) String name)
            throws AuthException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("name", name);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = new BankAccount(name);
        user.addBankAccount(bankAccount);
        datastore.save(bankAccount);
        datastore.save(user);

        return bankAccount;
    }

    /**
     * Removes given bank account
     * @param accountNo no of bank account which should be deleted
     * @throws BankServiceException if bank account cannot be deleted (user is not owning it or it does not exists)
     * @throws AuthException if authorization process fails
     * @throws ValidationException if parameter validation fails
     */
    @WebMethod
    public void deleteBankAccount(@WebParam(name = "accountNo") @XmlElement(required = true) String accountNo)
            throws BankServiceException, AuthException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("account no", accountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(accountNo).get();
        if (bankAccount == null) {
            throw new BankServiceException("Bank account with given no not exists");
        }
        if (user.containsBankAccount(bankAccount.getAccountNo())) {
            user.removeBankAccount(bankAccount.getAccountNo());
            datastore.delete(bankAccount);
            datastore.save(user);
        } else {
            throw new BankServiceException("User does not own this bank account");
        }
    }
}
