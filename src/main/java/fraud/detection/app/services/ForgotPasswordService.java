package fraud.detection.app.services;

import fraud.detection.app.dto.ForgotPasswordDTO;
import fraud.detection.app.dto.OtpDTO;
import fraud.detection.app.dto.SmsRequest;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;
@Slf4j
@Service
public class ForgotPasswordService {
    private final TwilioSmsSender twilioSmsSender;
    private final UniversalResponse response;
    private final UserRepository userRepository;
private final OtpDTO otpDTO;
    public ForgotPasswordService(TwilioSmsSender twilioSmsSender, UniversalResponse response, UserRepository userRepository, OtpDTO otpDTO) {
        this.twilioSmsSender = twilioSmsSender;
        this.response = response;
        this.userRepository = userRepository;
        this.otpDTO = otpDTO;
    }

    public UniversalResponse forgotPassword(ForgotPasswordDTO request){
        try {
            String checkedNumber = checkPhoneNumber(request.getPhoneNumber());
            if (checkedNumber.equals("Invalid phone number")) {
                return UniversalResponse.builder().message("Invalid PhoneNumber").data(null).status("1").build();
            }
            if (userRepository.findUserByMobileNumber(request.getPhoneNumber()) != null) {
                try {

                    Random random = new Random();
                    int otp = random.nextInt(9000) + 1000;

                    //SmsRequest smsRequest = new SmsRequest(phoneNumber.toString(),otp);
                    var smsRequest = SmsRequest.builder().phoneNumber(request.getPhoneNumber()).message(otp).build();
                    System.out.println(smsRequest);
                    //System.out.println(Expiry);
                    try {
                        twilioSmsSender.SendSms(smsRequest);
                        log.info("otp Sent");
                        otpDTO.setOtp(otp);
                        return UniversalResponse.builder().message("Success").status("1").data(otpDTO).build();

                    } catch (Exception ex) {
                        System.out.println("Failed to send Otp{}" + ex);
                        UniversalResponse.builder().message("Success").status("1").build();
                        //return ex.getMessage();
                    }
                } catch (Exception ex) {
                    System.out.println("Error{}" + ex);
                }
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
