package pl.poznan.put.bsr.bank.services;

import org.glassfish.jersey.internal.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.exceptions.AuthException;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.exceptions.ValidationException;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.User;
import pl.poznan.put.bsr.bank.models.bankOperations.*;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Web Service class responsible for bank operations on bank accounts
 * @author Kamil Walkowiak
 */
@WebService
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class BankOperationService {
    @Resource
    private WebServiceContext context;

    /**
     * Counts bank fee on given bank account
     * @param targetAccountNo no of bank account on which bank fee should be counted
     * @return created count fee bank operation
     * @throws ValidationException if parameter validation fails
     * @throws AuthException if authorization process fails
     * @throws BankServiceException if given bank account does not exists
     * @throws BankOperationException if bank operation parameters are invalid
     */
    @WebMethod
    public BankOperation countFee(@WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws ValidationException, AuthException, BankServiceException, BankOperationException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("receiver account no", targetAccountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if (bankAccount == null) {
            throw new BankServiceException("Target bank account does not exist");
        }

        Fee fee = new Fee(targetAccountNo);
        fee.doOperation(bankAccount);
        datastore.save(bankAccount);
        return fee;
    }

    /**
     * Makes payment on given bank account
     * @param title title of payment operation
     * @param amount amount of payment operation
     * @param targetAccountNo on of bank account on which payment should be made
     * @return created payment bank operation
     * @throws BankServiceException if given bank account does not exists
     * @throws BankOperationException if bank operation parameters are invalid
     * @throws ValidationException if parameter validation fails
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public BankOperation makePayment(@WebParam(name = "title") @XmlElement(required = true) String title,
                                     @WebParam(name = "amount") @XmlElement(required = true) String amount,
                                     @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("title", title);
            put("amount", amount);
            put("receiver account no", targetAccountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);
        double parsedAmount = ValidateParamsUtil.parseAmount(amount);
        checkTransferLimit(parsedAmount);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if (bankAccount == null) {
            throw new BankServiceException("Target bank account does not exist");
        }

        Payment payment = new Payment(title, parsedAmount, targetAccountNo);
        payment.doOperation(bankAccount);
        datastore.save(bankAccount);
        return payment;
    }

    /**
     * Makes withdrawal from given bank account
     * @param title title of withdrawal operation
     * @param amount amount of withdrawal operation
     * @param targetAccountNo no of bank account from which withdrawal should be made
     * @return created withdrawal bank operation
     * @throws BankServiceException if withdrawal cannot be made
     * (user does not own given bank account or bank account does not exists)
     * @throws BankOperationException if bank operation parameters are invalid
     * @throws ValidationException if parameter validation fails
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public BankOperation makeWithdrawal(@WebParam(name = "title") @XmlElement(required = true) String title,
                                        @WebParam(name = "amount") @XmlElement(required = true) String amount,
                                        @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("title", title);
            put("amount", amount);
            put("receiver account no", targetAccountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);
        double parsedAmount = ValidateParamsUtil.parseAmount(amount);
        checkTransferLimit(parsedAmount);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount bankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
        if (bankAccount == null) {
            throw new BankServiceException("Target bank account does not exist");
        }

        if (!user.containsBankAccount(targetAccountNo)) {
            throw new BankServiceException("Target account does not belong to user");
        }
        Withdrawal withdrawal = new Withdrawal(title, parsedAmount, targetAccountNo);
        withdrawal.doOperation(bankAccount);
        datastore.save(bankAccount);
        return withdrawal;
    }

    /**
     * Makes transfer from given internal source account to given internal/external target account
     * @param title title of transfer operation
     * @param amount amount of transfer operation
     * @param sourceAccountNo no of transfer source bank account
     * @param targetAccountNo no of transfer target bank account
     * @return created transfer operation
     * @throws BankServiceException if transfer cannot be made
     * (source/target bank account does not exist or are the same, user does not own source account,
     * transfer amount is higher than max limit or bank of target bank account is unknown)
     * @throws BankOperationException if bank operation parameters are invalid
     * @throws ValidationException if parameter validation fails
     * @throws AuthException if authorization process fails
     */
    @WebMethod
    public BankOperation makeTransfer(@WebParam(name = "title") @XmlElement(required = true) String title,
                                      @WebParam(name = "amount") @XmlElement(required = true) String amount,
                                      @WebParam(name = "sourceAccountNo") @XmlElement(required = true) String sourceAccountNo,
                                      @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException, ValidationException, AuthException {
        Map<String, String> parametersMap = new HashMap<String, String>() {{
            put("title", title);
            put("amount", amount);
            put("sender account no", sourceAccountNo);
            put("receiver account no", targetAccountNo);
        }};
        ValidateParamsUtil.validateParameters(parametersMap);
        double parsedAmount = ValidateParamsUtil.parseAmount(amount);
        checkTransferLimit(parsedAmount);

        Datastore datastore = DataStoreHandlerUtil.getInstance().getDataStore();
        User user = AuthUtil.getUserFromWebServiceContext(context, datastore);

        BankAccount sourceBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(sourceAccountNo).get();
        if (sourceBankAccount == null) {
            throw new BankServiceException("Source bank account does not exist");
        }
        if (!user.containsBankAccount(sourceAccountNo)) {
            throw new BankServiceException("Source account does not belong to user");
        }
        if (sourceAccountNo.equals(targetAccountNo)) {
            throw new BankServiceException("Target account is the same as source account");
        }

        Transfer outTransfer = new Transfer(title, parsedAmount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.OUT);

        if (targetAccountNo.substring(2, 10).equals(ConstantsUtil.BANK_ID)) {
            BankAccount targetBankAccount = datastore.find(BankAccount.class).field("accountNo").equal(targetAccountNo).get();
            if (targetBankAccount == null) {
                throw new BankServiceException("Target bank account does not exist");
            }
            Transfer inTransfer = new Transfer(title, parsedAmount, sourceAccountNo, targetAccountNo, Transfer.TransferDirection.IN);
            makeInternalTransfer(datastore, sourceBankAccount, targetBankAccount, inTransfer, outTransfer);
        } else {
            try {
                makeExternalTransfer(datastore, sourceBankAccount, outTransfer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outTransfer;
    }

    private void checkTransferLimit(double amount) throws BankServiceException {
        if (amount > 1000000) {
            throw new BankServiceException("Transfer amount is higher than max limit of 1 000 000");
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
        if (!bankToIpMap.containsKey(bankNo)) {
            throw new BankServiceException("Unknown bank of target account");
        }

        outTransfer.doOperation(sourceBankAccount);

        String charset = "UTF-8";
        String url = bankToIpMap.get(bankNo) + "/transfer";
        String data = "{" +
                "\"amount\":" + (int) (outTransfer.getAmount() * 100) + "," +
                "\"sender_account\":" + "\"" + outTransfer.getSourceAccountNo() + "\"," +
                "\"receiver_account\":" + "\"" + outTransfer.getTargetAccountNo() + "\"," +
                "\"title\":" + "\"" + outTransfer.getTitle() + "\"}";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        connection.setRequestProperty("Authorization", "Basic " +
                Base64.encodeAsString(ConstantsUtil.BANK_USERNAME + ":" + ConstantsUtil.BANK_PASSWORD));

        OutputStream requestBody = connection.getOutputStream();
        requestBody.write(data.getBytes(charset));
        requestBody.close();
        connection.connect();

        int status = connection.getResponseCode();
        if (status != 201) {
            InputStream response = connection.getErrorStream();
            if (response != null) {
                String message = new String(IOUtils.readFully(response, -1, true));
                JSONParser parser = new JSONParser();
                JSONObject obj = null;
                try {
                    obj = (JSONObject) parser.parse(message);
                    if (obj.containsKey("error")) {
                        throw new BankServiceException((String) obj.get("error"));
                    } else {
                        throw new BankServiceException("Unknown error occurs");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                throw new BankServiceException("Unknown error occurs");
            }
        } else {
            datastore.save(sourceBankAccount);
        }
    }
}