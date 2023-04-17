package fraud.detection.app.services;

import fraud.detection.app.dto.LipaBillDto;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LipaBillService {
    private final HelperUtility helperUtility;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
private UniversalResponse response;

    public LipaBillService(HelperUtility helperUtility
            , AccountRepository accountRepository
            , TransactionRepository transactionRepository) {
        this.helperUtility = helperUtility;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public UniversalResponse lipaBill(LipaBillDto request) {
        if (helperUtility.checkPin(request.getPayBillNo(), request.getPin())) {
            if (helperUtility.checkAccountBalance(request.getPayerNo(), request.getAmount())) {
                Account account = accountRepository.findByAccountNumber(request.getPayerNo());
                double updatedAccountBalance = account.getAccountBalance() - request.getAmount();
                //updating Accounts Table
                double BeforeAccountBalance = account.getAccountBalance();
                account.setBalanceBefore(BeforeAccountBalance);
                account.setAccountBalance(updatedAccountBalance);
                accountRepository.save(account);
                //Inserting Into transaction Table
                Transaction transaction = new Transaction();
                var transObj = transaction.builder()
                        .Debited(request.getAmount())
                        .Credited(request.getAmount())
                        .transactionType("LipaBill")
                        .senderAccount(request.getPayerNo())
                        .receiverAccount(request.getPayBillNo())
                        .Status("completed")
                        .transactionAmount(request.getAmount())
                        .ReferenceCode(helperUtility.getTransactionUniqueNumber()).
                        build();
            } else {
                UniversalResponse response= UniversalResponse.builder()
                        .message("You have Insufficient Balance")
                        .status("failed")
                        .data(request)
                        .build();
                return  response;
            }

        } else {
            //TODO return Entered wrong pin
            UniversalResponse response= UniversalResponse.builder()
                    .message("You have Enetred the wrong Pin")
                    .status("failed")
                    .data(request)
                    .build();
            return  response;
        }
        return response;
    }

}
