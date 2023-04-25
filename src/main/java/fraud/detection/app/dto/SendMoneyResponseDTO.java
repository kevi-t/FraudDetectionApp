package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SendMoneyResponseDTO {
    private String receiverAccountNumber;
    private double transactionAmount;
}
