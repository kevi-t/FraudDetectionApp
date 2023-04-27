package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.AccountStatementService;
import fraud.detection.app.services.DepositStatementService;
import fraud.detection.app.services.SendMoneyStatementService;
import fraud.detection.app.services.WithdrawStatementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fraud/app/account")
public class AccountStatementController {

    private final AccountStatementService accountStatementService;
    private final DepositStatementService depositStatementService;
    private final WithdrawStatementService withdrawStatementService;
    private final SendMoneyStatementService sendMoneyStatementService;

    @Autowired
    public AccountStatementController(AccountStatementService accountStatementService, DepositStatementService depositStatementService, WithdrawStatementService withdrawStatementService, SendMoneyStatementService sendMoneyStatementService) {
        this.accountStatementService = accountStatementService;
        this.depositStatementService = depositStatementService;
        this.withdrawStatementService = withdrawStatementService;
        this.sendMoneyStatementService = sendMoneyStatementService;
    }

    @PostMapping("/statement")
    public ResponseEntity<UniversalResponse> getAllUserTransactions(@Valid @RequestBody AccountStatementDTO request){

        try{
            return ResponseEntity.ok(accountStatementService.getAllUserTransactions(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/depositStatement")
    public ResponseEntity<UniversalResponse> getAllDepositUserTransactions(@Valid @RequestBody DepositStatementDTO request){

        try{
            return ResponseEntity.ok(depositStatementService.getAllDepositUserTransactions(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/withdrawStatement")
    public ResponseEntity<UniversalResponse> getAllWithdrawUserTransactions(@Valid @RequestBody WithdrawStatementDTO request){

        try{
            return ResponseEntity.ok(withdrawStatementService.getAllWithdrawUserTransactions(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/sendMoneyStatement")
    public ResponseEntity<UniversalResponse> getAllSendMoneyUserTransactions(@Valid @RequestBody DepositStatementDTO request){

        try{
            return ResponseEntity.ok(sendMoneyStatementService.getAllSendMoneyUserTransactions(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}