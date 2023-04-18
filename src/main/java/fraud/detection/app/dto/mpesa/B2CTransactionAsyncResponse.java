package fraud.detection.app.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.transform.Result;

@Data
    public class B2CTransactionAsyncResponse{

        @JsonProperty("Result")
        private Result result;
    }

