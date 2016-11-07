package pl.poznan.put.bsr.bank.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author Kamil Walkowiak
 */
@WebService
public class ExampleService {
    @WebMethod
    public int add(@WebParam() int value1, @WebParam() int value2) {
        return value1 + value2;
    }
}
