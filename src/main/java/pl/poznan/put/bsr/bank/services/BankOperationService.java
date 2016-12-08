package pl.poznan.put.bsr.bank.services;

import org.mongodb.morphia.Datastore;
import pl.poznan.put.bsr.bank.models.BankAccount;
import pl.poznan.put.bsr.bank.models.operations.Payment;
import pl.poznan.put.bsr.bank.services.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.services.exceptions.BankServiceException;
import pl.poznan.put.bsr.bank.utils.DataStoreHandlerUtil;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class BankOperationService {
    @WebMethod
    public void makePayment(@WebParam(name = "title") @XmlElement(required = true) String title,
                            @WebParam(name = "amount") @XmlElement(required = true) double amount,
                            @WebParam(name = "targetAccountNo") @XmlElement(required = true) String targetAccountNo)
            throws BankServiceException, BankOperationException {
        Payment payment = new Payment(title, amount, targetAccountNo);
        payment.doOperation();
    }
}
