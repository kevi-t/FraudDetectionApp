package fraud.detection.app.configurations;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private final static Logger LOGGER= LoggerFactory.getLogger(TwilioInitializer.class);
    private final TwilioConfiguration twilioConfig;

    @Autowired
    public TwilioInitializer(TwilioConfiguration twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getTwilioAccountSid(),twilioConfig.getTwilioAuthToken());
        LOGGER.info("twilio initialized....");
    }
}



