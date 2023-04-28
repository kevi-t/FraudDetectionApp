package fraud.detection.app.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
public class AccountStatementResponse {
    private List transactionList;

}
