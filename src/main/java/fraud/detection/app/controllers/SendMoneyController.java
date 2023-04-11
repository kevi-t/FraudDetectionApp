package fraud.detection.app.controllers;

import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.services.SendMoneyService;
import jakarta.validation.Valid;
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
public class SendMoneyController {

    private final SendMoneyService sendMoneyService;
    @Autowired
    public SendMoneyController(SendMoneyService sendMoneyService) {
        this.sendMoneyService = sendMoneyService;
    }

    @PostMapping("/sendmoney")
    public ResponseEntity<?> sendMoney(@Valid @RequestBody SendMoneyDTO request) {
       // System.out.println(request.getReceiverAccountNumber());
        try{
            //System.out.println(request.getReceiverAccountNumber());

            return ResponseEntity.ok(sendMoneyService.sendMoney(request));
        }
        catch (Exception ex){
            System.out.println("ERROR"+ex);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}