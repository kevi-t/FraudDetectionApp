package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountDTO;
import fraud.detection.app.dto.TransactionDTO;
import fraud.detection.app.services.AccountService;
import fraud.detection.app.services.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping("/Account-Balance")
    public ResponseEntity<?> checkBalance(@Valid @RequestBody AccountDTO request){
        return ResponseEntity.ok(accountService.checkBalance(request));
    }

    @PostMapping("/Deposit")
    public ResponseEntity<?> depositMoney(@Valid @RequestBody TransactionDTO request) {
        return ResponseEntity.ok(transactionService.depositMoney(request));
    }

    //    @PostMapping("/send-money")
//    public ResponseEntity<?> sendMoney(@Valid @RequestBody Account transferBalanceRequest) {
//        return ResponseEntity.ok(accountService.sendMoney(transferBalanceRequest));
//    }
}
