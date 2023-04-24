package fraud.detection.app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Column(nullable = false, updatable = false)
    private String transactionType;
    @Column(scale = 2,nullable = false, updatable = false)
    private double transactionAmount;
    @Column(nullable = false, updatable = false)
    private String Status;
    private double Debited;
    private double Credited;
    @Column(nullable = false, updatable = false)
    private String receiverAccount;
    @Column(nullable = false, updatable = false)
    private String senderAccount;
    @Column(nullable = false, updatable = false)
    private String ReferenceCode;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @DateTimeFormat(pattern ="yyyy:dd:MM HH:mm:ss")
    private LocalDateTime transactionDate;
}