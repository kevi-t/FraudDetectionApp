package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.AccountStatementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fraud/app/account")
public class AccountStatementController {

    private final AccountStatementService accountStatementService;

    @Autowired
    public AccountStatementController(AccountStatementService accountStatementService) {
        this.accountStatementService = accountStatementService;
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

}