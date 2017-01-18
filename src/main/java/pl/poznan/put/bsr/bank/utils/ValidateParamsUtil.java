package pl.poznan.put.bsr.bank.utils;

import pl.poznan.put.bsr.bank.exceptions.ValidationException;

import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
public abstract class ValidateParamsUtil {
    public static void validateParameters(Map<String, String> parametersMap) throws ValidationException {
        String errorMessage = "";

        for(Map.Entry<String, String> parameter: parametersMap.entrySet()) {
            String key = parameter.getKey();
            String value = parameter.getValue();
            if(value == null || value.length() == 0 || value.matches(".*\\p{C}.*") ||
                    (key.contains("account") && !(value.matches("[0-9]+") && value.length() == 26))) {
                errorMessage += key + ", ";
            }
        }

        if(errorMessage.length() > 0) {
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1, errorMessage.length() - 2);
            int index = errorMessage.lastIndexOf(",");
            if(index != -1) {
                errorMessage = errorMessage.substring(0, index) + " and" + errorMessage.substring(index + 1);
            }
            errorMessage += " is missing or invalid";
            throw new ValidationException(errorMessage);
        }
    }

    public static double parseAmount(String amount) throws ValidationException {
        amount = amount.replace(",", ".");
        try {
           return Double.parseDouble(amount);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Amount is invalid");
        }
    }
}
