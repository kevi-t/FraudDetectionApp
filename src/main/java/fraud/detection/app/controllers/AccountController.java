package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountDTO;
import fraud.detection.app.dto.DepositDTO;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.dto.WithdrawDTO;
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

    @PostMapping("/account-balance")
    public ResponseEntity<?> checkBalance(@Valid @RequestBody AccountDTO request){
        return ResponseEntity.ok(accountService.checkBalance(request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositMoney(@Valid @RequestBody DepositDTO request) {
        return ResponseEntity.ok(transactionService.depositMoney(request));
    }
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMoney(@Valid @RequestBody WithdrawDTO request ) {
        return ResponseEntity.ok(transactionService.withdrawMoney(request));
    }

    @PostMapping("/send-money")
    public ResponseEntity<?> sendMoney(@Valid @RequestBody SendMoneyDTO request) {
        return ResponseEntity.ok(transactionService.sendMoney(request));
    }
}
