package fraud.detection.app.models;

import jakarta.persistence.*;
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
    private String senderAccount;
    private String businessNo;
    private String receiverAccount;
    private String transactionType;
    private double transactionAmount;
    private String Status;
    private String Debited;
    private String Credited;
    private String ReferenceCode;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;
}