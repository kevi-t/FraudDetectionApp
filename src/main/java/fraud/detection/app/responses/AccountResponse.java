package fraud.detection.app.responses;

import lombok.*;
import org.springframework.stereotype.Component;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AccountResponse {
    private String status;
    private String message;
    private Object data2;
    private Object data;
}
