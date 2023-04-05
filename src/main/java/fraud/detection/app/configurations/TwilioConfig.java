package fraud.detection.app.configurations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class TwilioConfig {

    private String twilioAccountSid;
    private String trial_number;
    private String twilioAuthToken;

    public TwilioConfig(){

    }

    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }

    public void setTwilioAccountSid(String twilioAccountSid) {
        this.twilioAccountSid = twilioAccountSid;
    }

    public String getTrial_number() {
        return trial_number;
    }

    public void setTrial_number(String trial_number) {
        this.trial_number = trial_number;
    }

    public String getTwilioAuthToken() {
        return twilioAuthToken;
    }

    public void setTwilioAuthToken(String twilioAuthToken) {
        this.twilioAuthToken = twilioAuthToken;
    }
}
