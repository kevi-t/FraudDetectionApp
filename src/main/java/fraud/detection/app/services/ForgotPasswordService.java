package fraud.detection.app.services;

import fraud.detection.app.dto.OtpDTO;
import fraud.detection.app.dto.ResetPasswordDTO;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;
@Slf4j
@Service
public class ForgotPasswordService {
    private final TwilioSmsSender twilioSmsSender;
    private final UniversalResponse response;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
private final OtpDTO otpDTO;
    public ForgotPasswordService(TwilioSmsSender twilioSmsSender, UniversalResponse response, UserRepository userRepository, PasswordEncoder passwordEncoder, OtpDTO otpDTO) {
        this.twilioSmsSender = twilioSmsSender;
        this.response = response;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpDTO = otpDTO;
    }

    public UniversalResponse changePassword(ResetPasswordDTO request){
        try {
            String checkedNumber = checkPhoneNumber(request.getPhoneNumber());
            if (checkedNumber.equals("Invalid phone number")) {
                return UniversalResponse.builder().message("Invalid PhoneNumber").data(null).status("1").build();
            }
            if (userRepository.findUserByMobileNumber(checkedNumber) != null) {
                User user=userRepository.findUserByMobileNumber(checkedNumber);
                user.setPin(passwordEncoder.encode(request.getPin()));
                try {
                    userRepository.save(user);
                }catch (Exception e){
                    System.out.println("Errror"+ e);
                }
                 return UniversalResponse.builder()
                        .message("Pin reset successful")
                        .status("1")
                        .build();

        }else{
                return UniversalResponse.builder()
                        .message("Invalid User")
                        .status("1")
                        .build();
            }

        }
        catch (Exception ex){
            System.out.println("Error{}"+ex);
        }
        return response;

    }
}
