package fraud.detection.app.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LipaBillDto {
    private String pin;
    private String PayerNo;
    private String payBillNo;
    private float amount;

}
