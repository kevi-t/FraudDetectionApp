package fraud.detection.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    private String accountNumber;
    private String terminalId;
    private String transactionType;
    private double transactionAmount;
    private int transactionFraud;
    private int transactionFraudScenario;
    @CreationTimestamp
    private LocalDateTime transactionDate;
}