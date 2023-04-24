package fraud.detection.app.controllers;

import fraud.detection.app.dto.*;
import fraud.detection.app.models.Otp;
import fraud.detection.app.repositories.OtpRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.ForgotPasswordService;
import fraud.detection.app.services.TwilioSmsSender;
import fraud.detection.app.services.ValidationService;
import fraud.detection.app.utils.HelperUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@RestController
@Transactional
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/otp")
public class OtpController {

    private final TwilioSmsSender twilioSmsSender;
    private final OtpRepository otpRepository;
    public UniversalResponse response;
    public ValidationService validationService;
    private final OtpDTO otpDTO;
 private final HelperUtility helperUtility;
 private final ForgotPasswordService forgotPasswordService;
    @Autowired
    public OtpController(TwilioSmsSender twilioSmsSender, OtpRepository otpRepository, ValidationService validationService, OtpDTO otpDTO, HelperUtility helperUtility, ForgotPasswordService forgotPasswordService) {
        this.twilioSmsSender = twilioSmsSender;
        this.otpRepository = otpRepository;
        this.validationService = validationService;
        this.otpDTO = otpDTO;
        this.helperUtility = helperUtility;
        this.forgotPasswordService = forgotPasswordService;
    }
    @PostMapping("/forgot/password")
    public ResponseEntity<UniversalResponse>forgotPassword(@RequestBody ForgotPasswordDTO request) {
        System.out.println(request);
        return ResponseEntity.ok(forgotPasswordService.forgotPassword(request));
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

                try {

                    Random random = new Random();
                    int otp = random.nextInt(9000) + 1000;

                    //SmsRequest smsRequest = new SmsRequest(phoneNumber.toString(),otp);
                    var smsRequest= SmsRequest.builder().phoneNumber(request.getPhoneNumber()).message(otp).build();
                    System.out.println(smsRequest);
                    //System.out.println(Expiry);
                    try{
                        twilioSmsSender.SendSms(smsRequest);
                        log.info("otp Sent");
                        otpDTO.setOtp(otp);
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
            }

        }
        catch (Exception ex){
          System.out.println("Error{}"+ex);
        }
        return response;
    }


//    @PostMapping("/verify")
//    public UniversalResponse verifyOtp(@RequestBody VerifyOtpDTO request) {
//
//        Otp otp=otpRepository.findOtpByMobileNumber(request.getPhoneNumber());
//        System.out.println(request.getPhoneNumber());
//
//        if (otpRepository.findOtpByMobileNumber(request.getPhoneNumber())!=null)
//        {
//            LocalDateTime currentTime = LocalDateTime.now();
//            LocalDateTime ExpiryTime=otp.getOtpExpiryTime();
//            Duration duration = Duration.between(ExpiryTime, currentTime);// difference between two local date with times
//            long minutes = duration.toMinutes() % 60; // minutes between two local date with times
//            int SavedOTP=otp.getOtp();
//            System.out.println((minutes));
//            System.out.println((request.getOtp()));
//
//            if (SavedOTP!= (request.getOtp())){
//                return UniversalResponse.builder().message("You Entered the wrong OTP").status("1").build();
//            }
//            if (minutes==5) {
//                UniversalResponse response= UniversalResponse.builder().message("OTP verified").status("1").build();
//                //TODO: Add a method to delete the otp field in db here
//                otpRepository.deleteByMobileNumber(request.getPhoneNumber());
//                return response;
//            }
//            else if (minutes<5){
//                UniversalResponse response= UniversalResponse.builder().message("OTP Verified").status("1").build();
//                //TODO: Add a method to delete the otp field in db here
//                otpRepository.deleteByMobileNumber(request.getPhoneNumber());
//                return response;
//            }
//            else {
//                UniversalResponse response= UniversalResponse.builder().message("OTP as Expired").status("1").build();
//                try{
//                    otpRepository.deleteByMobileNumber(request.getPhoneNumber());
//                }
//                catch (Exception ex){
//                       System.out.println("Delete otp error{}"+ex);
//                }
//                //TODO: Add a method to delete the otp field in db here
//                return response;
//            }
//        }
//        else {
//            return UniversalResponse.builder().message("Username Does Not Exist").status("1").build();
//        }
//    }
}