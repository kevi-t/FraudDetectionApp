package fraud.detection.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twilio.twiml.messaging.Body;
import lombok.Data;

    @Data
    public class StkPushAsyncResponse{

        @JsonProperty("Body")
        private Body body;
    }

