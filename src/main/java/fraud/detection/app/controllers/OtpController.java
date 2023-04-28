package fraud.detection.app.controllers;

import fraud.detection.app.dto.OtpDTO;
import fraud.detection.app.dto.ResetPasswordDTO;
import fraud.detection.app.dto.SendOtpDTO;
import fraud.detection.app.dto.SmsRequest;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.OtpRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.ForgotPasswordService;
import fraud.detection.app.services.TwilioSmsSender;
import fraud.detection.app.utils.HelperUtility;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@RestController
@Transactional
@Slf4j
@RequestMapping("/otp")
public class OtpController {

    private final TwilioSmsSender twilioSmsSender;
    private final OtpRepository otpRepository;
    public UniversalResponse response;
    private final OtpDTO otpDTO;
    private final HelperUtility helperUtility;
    private final ForgotPasswordService forgotPasswordService;
    private final AccountRepository accountRepository;

    @Autowired
    public OtpController(TwilioSmsSender twilioSmsSender, OtpRepository otpRepository, OtpDTO otpDTO, HelperUtility helperUtility, ForgotPasswordService forgotPasswordService, AccountRepository accountRepository) {
        this.twilioSmsSender = twilioSmsSender;
        this.otpRepository = otpRepository;
        this.otpDTO = otpDTO;
        this.helperUtility = helperUtility;
        this.forgotPasswordService = forgotPasswordService;
        this.accountRepository = accountRepository;
    }
    @PostMapping("/forgot/password/reset")
    public ResponseEntity<UniversalResponse>resetPassword(@RequestBody ResetPasswordDTO request) {
        System.out.println(request);
        return ResponseEntity.ok(forgotPasswordService.changePassword(request));
    }
    @PostMapping("/send")
    public UniversalResponse sendOtp(@RequestBody SendOtpDTO request) {
        System.out.println(request);
        try{
            String checkedNumber = checkPhoneNumber(request.getPhoneNumber());
            if (checkedNumber.equals("Invalid phone number"))
            {
                return UniversalResponse.builder().message("Invalid PhoneNumber").data(null).status("1").build();
            }

            else {
if (accountRepository.findByAccountNumber(checkedNumber)!=null){
    try {

        Random random = new Random();
        int otp = random.nextInt(9000) + 1000;

        //SmsRequest smsRequest = new SmsRequest(phoneNumber.toString(),otp);
        var smsRequest= SmsRequest.builder().phoneNumber(checkedNumber).message(otp).build();
        System.out.println(smsRequest);
        //System.out.println(Expiry);
        try{
            twilioSmsSender.SendSms(smsRequest);
            log.info("otp Sent");
            otpDTO.setOtp(String.valueOf(otp));
            return UniversalResponse.builder().message("Success").status("1").data(otpDTO).build();

        }
        catch (Exception ex){
            System.out.println("Failed to send Otp{}"+ex);
            UniversalResponse.builder().message("failed").status("1").build();
            //return ex.getMessage();
        }
    }
    catch (Exception ex){
        System.out.println("Error{}"+ex);
    }
}else return UniversalResponse.builder().message("Account provided does not exist").status("1").build();

            }

        }
        catch (Exception ex){
          System.out.println("Error{}"+ex);
        }
        return response;
    }
    @PostMapping("/sendregisterotp")
    public UniversalResponse sendOtpRegister(@RequestBody SendOtpDTO request) {
        System.out.println(request);
        try{
            String checkedNumber = checkPhoneNumber(request.getPhoneNumber());
            if (checkedNumber.equals("Invalid phone number"))
            {
                return UniversalResponse.builder().message("Invalid PhoneNumber").data(null).status("0").build();
            }

            else {
                if (accountRepository.findByAccountNumber(checkedNumber)==null){
                    try {

                        Random random = new Random();
                        int otp = random.nextInt(9000) + 1000;

                        //SmsRequest smsRequest = new SmsRequest(phoneNumber.toString(),otp);
                        var smsRequest= SmsRequest.builder().phoneNumber(checkedNumber).message(otp).build();
                        System.out.println(smsRequest);
                        //System.out.println(Expiry);
                        try{
                            twilioSmsSender.SendSms(smsRequest);
                            log.info("otp Sent");
                            otpDTO.setOtp(String.valueOf(otp));
                            return UniversalResponse.builder().message("Success").status("1").data(otpDTO).build();

                        }
                        catch (Exception ex){
                            System.out.println("Failed to send Otp{}"+ex);
                            UniversalResponse.builder().message("failed").status("1").build();
                            //return ex.getMessage();
                        }
                    }
                    catch (Exception ex){
                        System.out.println("Error{}"+ex);
                    }
                }else return UniversalResponse.builder().message("Account provided Already exist").status("0").build();

            }

        }
        catch (Exception ex){
            System.out.println("Error{}"+ex);
        }
        return response;
    }
}