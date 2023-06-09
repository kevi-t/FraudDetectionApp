package fraud.detection.app.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

    @Data
    public class InternalStkPushRequest{
      private String accountNo;
        @JsonProperty("Amount")
        private double amount;

        @JsonProperty("PhoneNumber")
        private String phoneNumber;
        private String pin;
    }

