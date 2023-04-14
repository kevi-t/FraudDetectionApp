package fraud.detection.app.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

    @Data
    public class SimulateTransactionRequest {

        @JsonProperty("BusinessShortCode")
        private String businessShortCode;

        @JsonProperty("Password")
        private String password;

        @JsonProperty("Timestamp")
        private String timestamp;

        @JsonProperty("TransactionType")
        private String transactionType;

        @JsonProperty("Amount")
        private String amount;
        @JsonProperty("PartyA")
        private String partyA;
        @JsonProperty("PartyB")
        private String partyB;
        @JsonProperty("PhoneNumber")
        private String phoneNumber;

        @JsonProperty("AccountReference")
        private String accountReference;

    }

