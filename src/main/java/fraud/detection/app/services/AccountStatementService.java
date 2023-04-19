package fraud.detection.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.dto.TransactionResponse;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AccountStatementService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfiguration;
    public UniversalResponse response;
    public final HelperUtility helperUtility;
    String referenceCode = HelperUtility.referenceCodeGenerator();
    List<TransactionResponse> transactionResponses = new ArrayList<>();
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

    public List<TransactionResponse> getAllUserTransactions(AccountStatementDTO request) {

        try {

            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {
                try {

                    String account = request.getAccountNumber();
                    List<Transaction> transactions = transactionRepository.findBySenderAccount(account);
                        for (Transaction transaction : transactions) {
                            TransactionResponse transactionResponse = new TransactionResponse();
                            transactionResponse.setTransactionId(transaction.getTransactionId());
                            transactionResponse.setTransactionType(transaction.getTransactionType());
                            transactionResponse.setTransactionAmount(transaction.getTransactionAmount());
                            transactionResponse.setCredited(transaction.getCredited());
                            transactionResponse.setDebited(transaction.getDebited());
                            transactionResponse.setTransactionDate(transaction.getTransactionDate());
                            transactionResponse.setReferenceCode(transaction.getReferenceCode());
                            transactionResponse.setSenderAccount(transaction.getSenderAccount());
                            transactionResponse.setReceiverAccount(transaction.getReceiverAccount());
                            transactionResponses.add(transactionResponse);
                        }

                    System.out.println(transactions);

                    return transactionResponses;
                }
                catch (Exception ex) {
//                    return UniversalResponse.builder()
//                            .message("Request Processing Error")
//                            .status("failed")
//                            .build();
                }
            }
            else {
//                return UniversalResponse.builder()
//                        .message("Wrong pin!")
//                        .status("failed")
//                        .build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return transactionResponses;
    }
}