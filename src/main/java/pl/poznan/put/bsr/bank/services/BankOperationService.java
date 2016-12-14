package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import org.glassfish.jersey.internal.util.Base64;
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
import pl.poznan.put.bsr.bank.utils.*;
import sun.misc.IOUtils;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
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
            throws BankServiceException, BankOperationException, ValidationException, AuthException, IOException {
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
            throws BankOperationException, BankServiceException, IOException {
        String bankNo = outTransfer.getTargetAccountNo().substring(2, 10);
        Map<String, String> bankToIpMap = MapBankToIpUtil.getInstance().getBankToIpMap();
        if(!bankToIpMap.containsKey(bankNo)) {
            throw new BankServiceException("Unknown bank of target account");
        }

        outTransfer.doOperation(sourceBankAccount);

        String charset = "UTF-8";
        String url = "http://" + bankToIpMap.get(bankNo) + ":" + ConstantsUtil.REST_PORT + "/transfer";
        String data = "{" +
                "\"amount\":" + (int)outTransfer.getAmount()*100 + "," +
                "\"sender_account\":" + "\"" + outTransfer.getSourceAccountNo() + "\"," +
                "\"receiver_account\":" + "\"" + outTransfer.getTargetAccountNo() + "\"," +
                "\"title\":" + "\"" + outTransfer.getTitle() + "\"}";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        connection.setRequestProperty("Authorization", "Basic " +
                Base64.encodeAsString(ConstantsUtil.BANK_ID + ":" + ConstantsUtil.BANK_PASSWORD));

        OutputStream requestBody = connection.getOutputStream();
        requestBody.write(data.getBytes(charset));
        requestBody.close();
        connection.connect();

        int status = connection.getResponseCode();
        if(status != 201) {
            InputStream response = connection.getErrorStream();
            if(response != null) {
                String message = new String(IOUtils.readFully(response, -1, true));
                throw new BankServiceException(message);
            } else {
                throw new BankServiceException();
            }
        }
    }
}