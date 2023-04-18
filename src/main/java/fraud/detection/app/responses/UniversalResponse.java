package fraud.detection.app.responses;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UniversalResponse implements Serializable {

    private String status;
    private String message;
    private Object data;
    //TODO: implement how to display data to the client
}
