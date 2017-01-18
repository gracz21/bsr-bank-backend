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
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
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
    public BankAccount addBankAccount(@WebParam(name = "name") @XmlElement(required = true) String name)
            throws BankServiceException, AuthException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
           put("name", name);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = new BankAccount(name);
        user.addBankAccount(bankAccount);
        datastore.save(bankAccount);
        datastore.save(user);

        return bankAccount;
    }

    @WebMethod
    public void deleteBankAccount(@WebParam(name = "accountNo") @XmlElement(required = true) String accountNo)
            throws BankServiceException, AuthException, ValidationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("account no", accountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore =  DataStoreHandlerUtil.getInstance().getDataStore();
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
