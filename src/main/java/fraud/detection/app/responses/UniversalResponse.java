package fraud.detection.app.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class UniversalResponse implements Serializable {

    private String status;
    private String message;
    private Object data;
    //TODO: implement how to display data to the client
}
