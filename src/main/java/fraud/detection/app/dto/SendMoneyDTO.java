package fraud.detection.app.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class
SendMoneyDTO {
    //@NotEmpty(message = "This field 'Amount' should not be empty")
    private double Amount;
   // @NotEmpty(message = "This field 'TO' should not be empty")
    private String To;
    //@NotEmpty(message = "This field 'From' should not be empty")
    private String From;
    private String pin;
}
