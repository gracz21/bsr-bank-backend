package pl.poznan.put.bsr.bank.services;

import com.sun.xml.ws.developer.SchemaValidation;
import pl.poznan.put.bsr.bank.models.bankOperations.Payment;
import pl.poznan.put.bsr.bank.exceptions.BankOperationException;
import pl.poznan.put.bsr.bank.exceptions.BankServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Kamil Walkowiak
 */
@WebService
@SchemaValidation
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
