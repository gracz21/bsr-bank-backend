package pl.poznan.put.bsr.bank.utils;

/**
 * Class providing constants used across whole project
 * @author Kamil Walkowiak
 */
public abstract class ConstantsUtil {
    /**
     * Provided bank no
     */
    public static final String BANK_ID = "00109714";
    /**
     * REST service port
     */
    public static final int REST_PORT = 8080;
    /**
     * SOAP service port
     */
    public static final int SOAP_PORT = 8000;
    /**
     * Database server port
     */
    public static final int MONGODB_PORT = 8004;
    /**
     * Name of map bank no to ip bank config file
     */
    public static final String BANK_TO_IP_MAP_FILE_NAME = "bank_to_ip_map.txt";
    /**
     * Basic authentication bank login
     */
    public static final String BANK_USERNAME = "admin";
    /**
     * Basic authentication bank password
     */
    public static final String BANK_PASSWORD = "admin";
    /**
     * Bank fee amount
     */
    public static final double feeAmount = 10.00;
}
