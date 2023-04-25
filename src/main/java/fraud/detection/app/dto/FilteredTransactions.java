package fraud.detection.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FilteredTransactions {
    private String transactionType;
    private double transactionAmount;
    private String receiverAccount;
    private String ReferenceCode;
    private LocalDateTime transactionDate;
}
