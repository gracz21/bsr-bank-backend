package pl.poznan.put.bsr.bank.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Kamil Walkowiak
 */
public class MapBankToIpUtil {
    private static MapBankToIpUtil Instance = new MapBankToIpUtil();
    private Map<String, String> bankToIpMap;

    public void initialize() throws IOException, URISyntaxException {
        bankToIpMap = new HashMap<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Files.lines(Paths.get(classloader.getResource(ConstantsUtil.bankToIpMapFileName).toURI())).forEach(line -> {
            String[] splitLine = line.split("=");
            bankToIpMap.put(splitLine[0], splitLine[1]);
        });
    }

    public static MapBankToIpUtil getInstance() {
        return Instance;
    }

    public Map<String, String> getBankToIpMap() {
        return bankToIpMap;
    }
}
