package fraud.detection.app.models;
import jakarta.persistence.*;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class B2C_C2B_Entries {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long internalId;
    private String transactionType;

    @Column(unique = true)
    private String transactionId;
    private String billRefNumber;
    private String msisdn;
    private String amount;

    @Column(unique = true)
    private String conversationId;

    @Column(unique = true)
    private String originatorConversationId;
    private Date EntryDate;
    private String resultCode;
    private Serializable rawCallbackPayloadResponse;
}