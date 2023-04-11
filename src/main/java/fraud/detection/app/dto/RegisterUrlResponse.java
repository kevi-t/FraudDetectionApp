package fraud.detection.app.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class RegisterUrlResponse {

    @JsonProperty("errorCode")
    private String conversationID;

    @JsonProperty("errorMessage")
    private String responseDescription;

    @JsonProperty("OriginatorCoversationID")
    private String originatorCoversationID;
    @JsonProperty("requestId")
    private String requestId;
}
