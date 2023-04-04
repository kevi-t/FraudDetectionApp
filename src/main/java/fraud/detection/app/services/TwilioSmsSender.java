package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.SmsRequest;
import fraud.detection.app.models.Otp;
import fraud.detection.app.repositories.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TwilioSmsSender  {

    private final OtpRepository otpRepository;
    private final TwilioConfiguration twilioConfig;
    private final static Logger LOGGER= LoggerFactory.getLogger(TwilioSmsSender.class);

    @Autowired
    public TwilioSmsSender(OtpRepository otpRepository, TwilioConfiguration twilioConfig) {
        super();
        this.otpRepository = otpRepository;
        this.twilioConfig = twilioConfig;
    }

    public void SendSms(SmsRequest smsRequest) {
        try {
            LocalDateTime Expiry = LocalDateTime.now().plusMinutes(5);
            Message twilioMessage = Message.creator(new PhoneNumber(smsRequest.getPhoneNumber()),new PhoneNumber(twilioConfig.getTrial_number()),String.valueOf(smsRequest.getMessage())).create();

            Otp otp1= Otp.builder().otpExpiryTime(Expiry).mobileNumber(smsRequest.getPhoneNumber()).otp(smsRequest.getMessage()).build();
            otpRepository.save(otp1);
        }
        catch (Exception ex) {
            System.out.println("Error While Sending OTP"+ex);
        }
        // LOGGER.info("Send sms {}" + smsRequest);;
    }
}
        //TODO implement PhoneNumber Validator

