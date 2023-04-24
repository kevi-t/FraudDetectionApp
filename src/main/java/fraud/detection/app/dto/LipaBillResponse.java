package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class LipaBillResponse {
    private String RecievedBy;
    private double amount;
    private double balance;
}
