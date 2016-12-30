package pl.poznan.put.bsr.bank.utils;

import pl.poznan.put.bsr.bank.exceptions.ValidationException;

import java.util.Map;

/**
 * @author Kamil Walkowiak
 */
public abstract class ValidateParamsUtil {
    public static void validate(Map<String, Object> parametersMap) throws ValidationException {
        String errorMessage = "";
        for(Map.Entry<String, Object> parameter: parametersMap.entrySet()) {
            if(parameter.getValue() == null) {
                errorMessage += parameter.getKey() + ",";
            } else if(parameter.getValue() instanceof String) {
                String value = (String)parameter.getValue();
                if(value.length() == 0) {
                    errorMessage += parameter.getKey() + ", ";
                }
            }
        }
        if(errorMessage.length() > 0) {
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + errorMessage.substring(1, errorMessage.length() - 2);
            int index = errorMessage.lastIndexOf(",");
            if(index != -1) {
                errorMessage = errorMessage.substring(0, index) + " and" + errorMessage.substring(index + 1);
            }
            errorMessage += " is missing or is invalid";
            throw new ValidationException(errorMessage);
        }
    }
}
