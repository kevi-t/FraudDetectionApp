package fraud.detection.app.controllers;

import fraud.detection.app.dto.StatementDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.CheckBalanceService;
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
@RequestMapping("/fraud/app/account")
@Validated
@Slf4j
public class CheckBalanceController {

    private final CheckBalanceService checkBalanceService;

    @Autowired
    public CheckBalanceController(CheckBalanceService checkBalanceService) {
        this.checkBalanceService = checkBalanceService;
    }

    @PostMapping("/balance")
    public ResponseEntity<UniversalResponse> checkBalance(@Valid @RequestBody StatementDTO request){
        try{
            return ResponseEntity.ok(checkBalanceService.checkBalance(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}