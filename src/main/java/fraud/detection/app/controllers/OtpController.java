package fraud.detection.app.controllers;

import fraud.detection.app.dto.SendOtpDTO;
import fraud.detection.app.dto.SmsRequest;
import fraud.detection.app.dto.VerifyOtpDTO;
import fraud.detection.app.models.Otp;
import fraud.detection.app.repositories.OtpRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.TwilioSmsSender;
import fraud.detection.app.services.ValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;


@RestController
@Transactional
@RequiredArgsConstructor
@RequestMapping("/otp")
public class OtpController {

    private final TwilioSmsSender twilioSmsSender;
    private final OtpRepository otpRepository;
    public UniversalResponse response;
    public ValidationService validationService;

    @Autowired
    public OtpController(TwilioSmsSender twilioSmsSender, OtpRepository otpRepository,  ValidationService validationService) {
        this.twilioSmsSender = twilioSmsSender;
        this.otpRepository = otpRepository;
        this.validationService = validationService;
    }

    @PostMapping("/send")
    public Object sendOtp(@RequestBody SendOtpDTO request) {
        System.out.println(request);
        try{
            if (validationService.isValidPhoneNumber(request.getPhoneNumber())==true)
            {
                try {

                    Random random = new Random();
                    int otp = random.nextInt(9000) + 1000;
                    //SmsRequest smsRequest = new SmsRequest(phoneNumber.toString(),otp);
                    SmsRequest smsOBj = new SmsRequest();
                    var smsRequest=smsOBj.builder().phoneNumber(request.getPhoneNumber()).message(otp).build();
                    System.out.println(smsRequest);
                    //System.out.println(Expiry);
                    try{
                        twilioSmsSender.SendSms(smsRequest);
                    }
                    catch (Exception ex){
                        System.out.println("Failed to send Otp{}"+ex);
                        //return ex.getMessage();
                    }
                }
                catch (Exception ex){
                    System.out.println("Error{}"+ex);
                }
            }
            else {
                return UniversalResponse.builder().message("Invalid PhoneNumber").status(0).build();
            }
        }
        catch (Exception ex){
          System.out.println("Error{}"+ex);
        }
        return response;
    }


    @PostMapping("/verify")
    public UniversalResponse verifyOtp(@RequestBody VerifyOtpDTO request) {

        Otp otp=otpRepository.findOtpByMobileNumber(request.getPhoneNumber());
        System.out.println(request.getPhoneNumber());

        if (otpRepository.findOtpByMobileNumber(request.getPhoneNumber())!=null)
        {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime ExpiryTime=otp.getOtpExpiryTime();
            Duration duration = Duration.between(ExpiryTime, currentTime);// difference between two local date with times
            long minutes = duration.toMinutes() % 60; // minutes between two local date with times
            int SavedOTP=otp.getOtp();
            System.out.println((minutes));
            System.out.println((request.getOtp()));

            if (SavedOTP!= (request.getOtp())){
                return UniversalResponse.builder().message("You Entered the  OTP").status(0).build();
            }
            if (minutes==5) {
                UniversalResponse response= UniversalResponse.builder().message("OTP verified").status(0).build();
                //TODO: Add a method to delete the otp field in db here
                otpRepository.deleteByMobileNumber(request.getPhoneNumber());
                return response;
            }
            else if (minutes<5){
                UniversalResponse response= UniversalResponse.builder().message("OTP Verified").status(0).build();
                //TODO: Add a method to delete the otp field in db here
                otpRepository.deleteByMobileNumber(request.getPhoneNumber());
                return response;
            }
            else {
                UniversalResponse response= UniversalResponse.builder().message("OTP as Expired").status(0).build();
                try{
                    otpRepository.deleteByMobileNumber(request.getPhoneNumber());
                }
                catch (Exception ex){
                       System.out.println("Delete otp error{}"+ex);
                }
                //TODO: Add a method to delete the otp field in db here
                return response;
            }
        }
        else {
            return UniversalResponse.builder().message("Username Does Not Exist").status(0).build();
        }
    }
}