package fraud.detection.app.configurations;
    import fraud.detection.app.dto.AcknowledgeResponse;
    import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "mpesa.daraja")
    public class MpesaConfiguration {

        private String consumerKey;
        private String consumerSecret;
        private String grantType;
        private String oauthEndpoint;
        private String shortCode;
        private String responseType;
        private String confirmationUrl;
        private String validationUrl;
        private String registerUrlEndpoint;
        private String stkPassKey;
        private String stkPushShortCode;
        private String stkPushRequestUrl;
        private String stkPushRequestCallbackUrl;
        @Override
        public String toString() {
            return String.format("{consumerKey='%s', consumerSecret='%s', grantType='%s', oauthEndpoint='%s'}",
                    consumerKey, consumerSecret, grantType, oauthEndpoint);
        }

        @Bean
        public AcknowledgeResponse getAcknowledgeResponse() {
            AcknowledgeResponse acknowledgeResponse = new AcknowledgeResponse();
            acknowledgeResponse.setMessage("Success");
            return acknowledgeResponse;
        }
}
