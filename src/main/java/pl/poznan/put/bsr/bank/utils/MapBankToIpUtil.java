package pl.poznan.put.bsr.bank.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Util class responsible for handling mapping bank no to IP
 * @author Kamil Walkowiak
 */
public class MapBankToIpUtil {
    private static MapBankToIpUtil Instance = new MapBankToIpUtil();
    private Map<String, String> bankToIpMap;

    /**
     * Initialize map bank no to IP class instance
     */
    public void initialize() {
        bankToIpMap = new HashMap<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            Files.lines(Paths.get(classloader.getResource(ConstantsUtil.BANK_TO_IP_MAP_FILE_NAME).toURI())).forEach(line -> {
                String[] splitLine = line.split("=");
                bankToIpMap.put(splitLine[0], splitLine[1]);
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get map bank no to IP class instance
     * @return instance of class
     */
    public static MapBankToIpUtil getInstance() {
        return Instance;
    }

    /**
     * Get map of bank on to IP records
     * @return map of bank on to IP records
     */
    public Map<String, String> getBankToIpMap() {
        return bankToIpMap;
    }
}
