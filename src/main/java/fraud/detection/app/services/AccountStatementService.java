package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountStatementService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfiguration;
    public UniversalResponse response;
    public final HelperUtility helperUtility;

    @Autowired
    public AccountStatementService(TransactionRepository transactionRepository,
                                   AccountRepository accountRepository,
                                   TwilioConfiguration twilioConfiguration,
                                   HelperUtility helperUtility) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse getAllUserTransactions(AccountStatementDTO request) {

        try {
            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {

                try {
                    String account = request.getAccountNumber();
                    List<Transaction> transactions = transactionRepository.findBySenderAccountOrReceiverAccount(account,account);

                    return UniversalResponse.builder()
                            .message("Request Successful")
                            .data(transactions)
                            .status("0")
                            .build();
                }
                catch (Exception ex) {
                    return UniversalResponse.builder()
                            .message("Request Processing Error")
                            .status("1")
                            .build();
                }
            }
            else {
                return UniversalResponse.builder()
                        .message("Wrong pin!")
                        .status("1")
                        .build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}