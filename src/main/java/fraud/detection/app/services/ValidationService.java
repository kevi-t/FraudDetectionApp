package fraud.detection.app.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
@Service
public class ValidationService {

    private static final String KENYA_PHONE_REGEX = "^(\\+254|0)7\\d{8}$|^\\+254[1]\\d{8}$|^\\+254[7]\\d{8}$";

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        // Remove any non-digit characters from the phone number
        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");

        // Validate the format of the phone number
        if (!Pattern.matches(KENYA_PHONE_REGEX, phoneNumber)) {
            return false;
        }

        // Validate the length of the phone number
        if (phoneNumber.length() != 10) {
            return false;
        }

        // Other validations can be added here, such as checking for unique or blacklisted numbers
        // If all validations pass, return true
        return true;
    }
    // other validation methods
}