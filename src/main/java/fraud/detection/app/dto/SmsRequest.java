package fraud.detection.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SmsRequest {

    private  String phoneNumber;//destination phone number
    private  int message;

    @Override
    public String toString() {
        return "SmsRequest{" +"phoneNumber='" + phoneNumber + '\'' +", message=" + message +'}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMessage() {
        return message;
    }
}
