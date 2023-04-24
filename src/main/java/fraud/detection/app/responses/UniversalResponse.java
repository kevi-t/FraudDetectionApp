package fraud.detection.app.responses;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UniversalResponse  {
    private String status;
    private String message;
    private Object data;
    //TODO: implement how to display data to the client
}
