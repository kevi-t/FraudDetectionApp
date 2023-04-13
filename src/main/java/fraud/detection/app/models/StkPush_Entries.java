
package fraud.detection.app.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StkPush_Entries {
    @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long internalId;
        @Column(unique = true)
        private String checkoutRequestID;
        private String responseCode;
        private String customerMessage;
        @Column(unique = true)
        private String merchantRequestID;
        @Column(unique = true)
        private String responseDescription;
       // private Serializable requestBody;
}