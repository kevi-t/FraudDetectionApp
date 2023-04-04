package fraud.detection.app.controllers;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.services.SendMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraud/app")
@Validated
@Slf4j
public class SendMoney {
    @Autowired
    SendMoneyService sendMoneyService;
    @PostMapping("/sendmoney")
    public ResponseEntity<?> Register(@RequestBody SendMoneyDTO request)
    {
        request.setTo("+254 112016790");
        request.setAmount(200);
        request.setFrom("+254 112016791");
        try{
            return ResponseEntity.ok(sendMoneyService.sendMoney(request));
        }catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST)
                    ;}


    }
}
