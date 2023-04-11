package fraud.detection.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPin {
   private String acountNo;
   private String Pin;
}
