package fraud.detection.app.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegisterDTO {

    private Integer otp;
    @NotEmpty(message = "This field 'Pin' should not be empty")
    private String pin;
    @NotEmpty(message = "This field 'firstName' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed in field firstname")
    private String firstName;
    @NotEmpty(message = "This field 'middleName' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed in field middle name")
    private String middleName;
    @NotEmpty(message = "This field 'lastName' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowedin field lastname ")
    private String lastName;
    @NotEmpty(message = "This field 'dateOfBirth' should not be empty")
    private String dateOfBirth;
    @NotEmpty(message = "This field 'gender' should not be empty")
    private String gender;
    @NotEmpty(message = "This field 'occupation' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed field occupation")
    private String occupation;

    //Contact Information
    @NotEmpty(message = "This field 'email' should not be empty")
    @Email(message = "Please enter a valid email address")
    private String email;
    @NotEmpty(message = "This field 'mobileNumber' should not be empty")
    private String mobileNumber;
    @NotEmpty(message = "This field 'permanentAddress' should not be empty")
    @Pattern(regexp = "[0-9\\-]+", message = "Please enter a valid number with optional dashes")
    private String permanentAddress;
    @NotEmpty(message = "This field 'currentAddress' should not be empty")
    @Pattern(regexp = "[0-9\\-]+", message = "Please enter a valid number with optional dashes")
    private String currentAddress;
    @NotEmpty(message = "This field 'pinCode' should not be empty")
    @Pattern(regexp = "[0-9\\-]+", message = "Please enter a valid number with optional dashes")
    private String pinCode;
    @NotEmpty(message = "This field 'city' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed field city")
    private String city;
    @NotEmpty(message = "This field 'state' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed in field state")
    private String state;
    @NotEmpty(message = "This field 'country' should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed in field country")
    private String country;

    public interface Create {

    }
}
