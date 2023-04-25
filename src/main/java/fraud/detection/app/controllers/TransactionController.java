package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.dto.TransactionDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fraud/app/account")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/balanceSheet")
    public ResponseEntity<UniversalResponse> getIncomeAndExpenses(@Valid @RequestBody TransactionDTO request){

        try{
            return ResponseEntity.ok(transactionService.getIncomeAndExpenses(request));
        }
        catch (Exception ex){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
