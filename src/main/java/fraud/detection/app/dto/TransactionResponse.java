package fraud.detection.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Data

public class TransactionResponse {
    private Long transactionId;
    private String transactionType;
    private double transactionAmount;
    private String Status;
    private double Debited;
    private double Credited;
    private String receiverAccount;
    private String senderAccount;
    private String ReferenceCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;
}
