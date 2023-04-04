package fraud.detection.app.dto;

import jakarta.validation.constraints.NotEmpty;
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
    //@NotEmpty(message = "This field 'model' should not be empty")
    private Integer otp;
    @NotEmpty(message = "This field 'Password' should not be empty")
    private String Pin;
    @NotEmpty(message = "This field 'firstName' should not be empty")
    private String firstName;
    @NotEmpty(message = "This field 'middleName' should not be empty")
    private String middleName;
    @NotEmpty(message = "This field 'lastName' should not be empty")
    private String lastName;
    @NotEmpty(message = "This field 'dateOfBirth' should not be empty")
    private String dateOfBirth;
    @NotEmpty(message = "This field 'gender' should not be empty")
    private String gender;
    @NotEmpty(message = "This field 'occupation' should not be empty")
    private String occupation;
    @NotEmpty(message = "This field 'parmanentAddress' should not be empty")
    //    Contact Information
    private String permanentAddress;
    @NotEmpty(message = "This field 'currentAddress' should not be empty")
    private String currentAddress;
    @NotEmpty(message = "This field 'mobileNumber' should not be empty")
    private String mobileNumber;
    @NotEmpty(message = "This field 'email' should not be empty")
    private String email;
    @NotEmpty(message = "This field 'pinCode' should not be empty")
    private String pinCode;
    @NotEmpty(message = "This field 'city' should not be empty")
    private String city;
    @NotEmpty(message = "This field 'state' should not be empty")
    private String state;
    @NotEmpty(message = "This field 'country' should not be empty")
    private String country;
    public interface Create {

    }
}
