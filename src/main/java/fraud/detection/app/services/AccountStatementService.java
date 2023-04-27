package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.AccountStatementDTO;
import fraud.detection.app.dto.FilteredTransactions;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AccountStatementService {

    private final TransactionRepository transactionRepository;
    private final TwilioConfiguration twilioConfiguration;
    public UniversalResponse response;
    public final HelperUtility helperUtility;

    @Autowired
    public AccountStatementService(TransactionRepository transactionRepository,
                                   TwilioConfiguration twilioConfiguration,
                                   HelperUtility helperUtility) {
        this.transactionRepository = transactionRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse getAllUserTransactions(AccountStatementDTO request) {

        try {
            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {

                try {
                    String account = request.getAccountNumber();
                    List<Transaction> transactions = transactionRepository.findBySenderAccountAndStatusOrReceiverAccountAndStatus(account,"success",account,"success");

                    double totalIncome = 0.00;
                    double totalExpense = 0.00;
                    for (Transaction transaction : transactions) {
                        if ("DEPOSIT".equalsIgnoreCase(transaction.getTransactionType())) {
                            totalIncome += transaction.getTransactionAmount();
                        }
                        else {
                            totalExpense += transaction.getTransactionAmount();
                        }
                    }
                    BigDecimal income = BigDecimal.valueOf(totalIncome).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal expense = BigDecimal.valueOf(totalExpense).setScale(2, BigDecimal.ROUND_HALF_UP);

                    Map<String, BigDecimal> result = new HashMap<>();
                    result.put("totalIncome", income);
                    result.put("totalExpense", expense);

                    // Create a new list to store filtered transactions
                    List<FilteredTransactions> filteredTransactions = new ArrayList<>();

                    // Loop through the transactions and extract the desired fields
                    for (Transaction transaction : transactions) {
                        FilteredTransactions filteredTransaction = new FilteredTransactions();
                        if (transaction.getTransactionType()!=null){
                            filteredTransaction.setTransactionType(transaction.getTransactionType());
                        }
                        if (transaction.getReceiverAccount()!=null){
                            filteredTransaction.setReceiverAccount(transaction.getReceiverAccount());
                        }
                        if (transaction.getReferenceCode()!=null){
                            filteredTransaction.setReferenceCode(transaction.getReferenceCode());
                        }
                        if (transaction.getTransactionDate()!=null){
                            filteredTransaction.setTransactionDate(transaction.getTransactionDate());
                        }
                        filteredTransaction.setTransactionAmount(transaction.getTransactionAmount());

                        // Add the filtered transaction to the filteredTransactions list
                        filteredTransactions.add(filteredTransaction);
                    }

                    return UniversalResponse.builder()
                            .message("Request Successful")
                            .data(filteredTransactions)
                            .status("1")
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