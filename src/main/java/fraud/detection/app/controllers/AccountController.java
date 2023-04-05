package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountDTO;
import fraud.detection.app.dto.DepositDTO;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.dto.WithdrawDTO;
import fraud.detection.app.services.AccountService;
import fraud.detection.app.services.DepositService;
import fraud.detection.app.services.SendMoneyService;
import fraud.detection.app.services.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AccountController {

    private final DepositService depositService;
    private final AccountService accountService;
    private final WithdrawService withdrawMoney;
    private final SendMoneyService  sendMoneyService;

    @PostMapping("/account-balance")
    public ResponseEntity<?> checkBalance(@Valid @RequestBody AccountDTO request){
        return ResponseEntity.ok(accountService.checkBalance(request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositMoney(@Valid @RequestBody DepositDTO request) {
        return ResponseEntity.ok(depositService.depositMoney(request));
    }
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMoney(@Valid @RequestBody WithdrawDTO request ) {
        return ResponseEntity.ok(withdrawMoney.withdrawMoney(request));
    }

    @PostMapping("/send-money")
    public ResponseEntity<?> sendMoney(@Valid @RequestBody SendMoneyDTO request) {
        return ResponseEntity.ok(sendMoneyService.sendMoney(request));
    }
}
