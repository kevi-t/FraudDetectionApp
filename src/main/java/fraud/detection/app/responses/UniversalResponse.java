package fraud.detection.app.responses;

import fraud.detection.app.models.Transaction;
import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UniversalResponse  {

    private String status;
    private String message;
    private Object data;
    //TODO: implement how to display data to the client
}
