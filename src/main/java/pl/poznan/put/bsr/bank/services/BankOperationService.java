package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.handlers.SchemaValidationHandler;
import pl.poznan.put.bsr.bank.models.Session;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.models.bankOperations.Payment;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.models.bankOperations.Withdrawal;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;
import pl.poznan.put.bsr.bank.utils.SAXExceptionToValidationExceptionUtil;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.WebServiceContext;

/**
 * @author Kamil Walkowiak
 */
@WebService
@SchemaValidation(handler = SchemaValidationHandler.class)
public class BankOperationService {
    @Resource
    private WebServiceContext context;

    @WebMethod
    public void makePayment(@WebParam(name = "title") @XmlElement(required = true) String title,
                            @WebParam(name = "amount") @XmlElement(required = true) double amount,
                            @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());
        AuthUtil.getSessionIdFromHeaders(context);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        Payment payment = new Payment(title, amount, targetAccountNo);
        payment.doOperation(datastore);
    }

    @WebMethod
    public void makeWithdrawal(@WebParam(name = "title") @XmlElement(required = true) String title,
                               @WebParam(name = "amount") @XmlElement(required = true) double amount,
                               @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        String sessionId = AuthUtil.getSessionIdFromHeaders(context);
        Session session = AuthUtil.getSessionObject(sessionId);
        User user = session.getUser();

        if(user != null) {
            if(user.containsBankAccount(targetAccountNo)) {
                Withdrawal withdrawal = new Withdrawal(title, amount, targetAccountNo);
                withdrawal.doOperation(datastore);
            } else {
                throw new BankServiceException("Target account does not belong to user or does not exist");
            }
        } else {
            datastore.delete(session);
            throw new BankServiceException("User assigned to this session not exists");
        }
    }
}
