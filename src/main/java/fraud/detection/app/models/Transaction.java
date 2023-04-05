package fraud.detection.app.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int TransactionID;
    private String receiver;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private float AMOUNT;
    private String Transaction_Type;
    private String Status;
    private String Debited;
    private String Credited;
    private String ReferenceCode;
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "Account_No", referencedColumnName = "accountNo")
   // private Account account;
    private String sender;

}
