package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.handlers.SchemaValidationHandler;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.models.bankOperations.Payment;
import pl.poznan.put.bsr.bank.models.bankOperations.Transfer;
import pl.poznan.put.bsr.bank.models.bankOperations.Withdrawal;
import pl.poznan.put.bsr.bank.utils.AuthUtil;
import pl.poznan.put.bsr.bank.utils.ConstantsUtil;
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

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if(bankAccount == null) {
            throw new BankServiceException("Target bank account does not exist");
        }

        Payment payment = new Payment(title, amount, targetAccountNo);
        payment.doOperation(bankAccount);
        datastore.save(bankAccount);
    }

    @WebMethod
    public void makeWithdrawal(@WebParam(name = "title") @XmlElement(required = true) String title,
                               @WebParam(name = "amount") @XmlElement(required = true) double amount,
                               @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if(bankAccount == null) {
            throw new BankServiceException("Target bank account does not exist");
        }

        if(!user.containsBankAccount(targetAccountNo)) {
            throw new BankServiceException("Target account does not belong to user");
        }
        Withdrawal withdrawal = new Withdrawal(title, amount, targetAccountNo);
        withdrawal.doOperation(bankAccount);
        datastore.save(bankAccount);
    }

    @WebMethod
    public void makeTransfer(@WebParam(name = "title") @XmlElement(required = true) String title,
                             @WebParam(name = "amount") @XmlElement(required = true) double amount,
                             @WebParam(name = "sourceAccountNo") @XmlElement(required = true) String sourceAccountNo,
                             @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        SAXExceptionToValidationExceptionUtil.parseExceptions(context.getMessageContext());

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount sourceBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(sourceAccountNo).get();
        if(sourceBankAccount == null) {
            throw new BankServiceException("Source bank account does not exist");
        }
        if(!user.containsBankAccount(sourceAccountNo)) {
            throw new BankServiceException("Source account does not belong to user");
        }

        Transfer outTransfer = new Transfer(title, amount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.OUT);

        if(targetAccountNo.substring(2, 10).equals(ConstantsUtil.BANK_ID)) {
            BankAccount targetBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
            if(targetBankAccount == null) {
                throw new BankServiceException("Target bank account does not exist");
            }
            Transfer inTransfer = new Transfer(title, amount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.IN);
            makeInternalTransfer(datastore, sourceBankAccount, targetBankAccount, inTransfer, outTransfer);
        } else {
            makeExternalTransfer(datastore, sourceBankAccount, outTransfer);
        }
    }

    private void makeInternalTransfer(Datastore datastore, BankAccount sourceBankAccount, BankAccount targetBankAccount,
                                      Transfer inTransfer, Transfer outTransfer)
            throws BankOperationException {
        outTransfer.doOperation(sourceBankAccount);
        inTransfer.doOperation(targetBankAccount);

        datastore.save(sourceBankAccount);
        datastore.save(targetBankAccount);
    }

    private void makeExternalTransfer(Datastore datastore, BankAccount sourceBankAccount, Transfer outTransfer)
            throws BankOperationException {
        outTransfer.doOperation(sourceBankAccount);
    }
}