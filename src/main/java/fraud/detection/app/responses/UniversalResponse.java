package fraud.detection.app.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class UniversalResponse implements Serializable {

    private int status;
    private String message;
    private String data;
    //TODO: implement how to display data to the client
}
