package fraud.detection.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SendMoneyMessage {

    private  String receiverPhoneNumber;//destination phone number
    private  String senderPhoneNumber;
    private  double message;
}
