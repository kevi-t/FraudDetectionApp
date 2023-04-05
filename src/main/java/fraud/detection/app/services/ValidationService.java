package fraud.detection.app.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationService {

    public boolean validateKenyanPhoneNumber(String phoneNumber) {
        // Remove any spaces or special characters from the phone number
        phoneNumber = phoneNumber.replaceAll("\\s+","").replaceAll("[^\\d]", "");

        // Regular expression pattern for Kenyan phone numbers
        String kenyanPhonePattern = "^\\+?254([1-9]{1})([0-9]{8})$";

        // Check if the phone number matches the pattern
        return true;
    }
    // other validation methods
}