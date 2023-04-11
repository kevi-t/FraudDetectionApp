package fraud.detection.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

    @Data
    public class Body{

        @JsonProperty("stkCallback")
        private StkCallback stkCallback;
    }

