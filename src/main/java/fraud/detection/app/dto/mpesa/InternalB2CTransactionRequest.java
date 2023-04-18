package fraud.detection.app.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
    public class InternalB2CTransactionRequest{

        @JsonProperty("Remarks")
        private String remarks;

        @JsonProperty("Amount")
        private String amount;

        @JsonProperty("Occassion")
        private String occassion;

        @JsonProperty("PartyB")
        private String partyB;
    }

