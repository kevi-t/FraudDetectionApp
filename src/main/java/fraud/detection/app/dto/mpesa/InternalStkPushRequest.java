package fraud.detection.app.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

    @Data
    public class InternalStkPushRequest{

        @JsonProperty("Amount")
        private double amount;
        @JsonProperty("PhoneNumber")
        private String phoneNumber;
        @NotNull(message = "Enter pin")
        private String pin;
    }

