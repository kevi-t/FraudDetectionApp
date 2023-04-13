package fraud.detection.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
    @Service
    public class StkPushSyncResponse{

        @JsonProperty("MerchantRequestID")
        private String merchantRequestID;

        @JsonProperty("ResponseCode")
        private String responseCode;

        @JsonProperty("CustomerMessage")
        private String customerMessage;

        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestID;

        @JsonProperty("ResponseDescription")
        private String responseDescription;
    }

