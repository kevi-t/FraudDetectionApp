package fraud.detection.app.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="accounts")
public class Account {
    @Id
            @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    //TODO Add created at (TimeStamp) columns
    private double balance;
    private double balanceBefore;
    private String openedBy;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountNo", referencedColumnName = "mobileNumber")
    private User user;
   // @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   // private List<Transaction> transaction;
    @Column(insertable=false, updatable=false)
    private String accountNo;
}
