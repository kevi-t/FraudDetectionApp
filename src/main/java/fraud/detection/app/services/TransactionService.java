package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.TransactionDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    public UniversalResponse response;
    public final HelperUtility helperUtility;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              HelperUtility helperUtility) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse getIncomeAndExpenses(TransactionDTO request) {
        try {
            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {

                try {
                    String account = request.getAccountNumber();

                    List<Transaction> transactions = transactionRepository.findBySenderAccountOrReceiverAccount(account, account);
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
                    Map<String, Double> result = new HashMap<>();
                    result.put("totalIncome", totalIncome);
                    result.put("totalExpense", totalExpense);
                    return UniversalResponse.builder()
                            .message("Request Successful")
                            .data(result)
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


//    Account account=accountRepository.findByAccountNumber(request.getAccountNumber());
//    public Map<String, Double> fetchTransactionsByAccount(Account accountNumber, String type) {
//
//        List<Transaction> transactions = null;
//        if (accountNumber== transactionRepository.findByReceiverAccount())
//        if ("RECEIVER".equalsIgnoreCase(type)) {
//            transactions = transactionRepository.findByReceiverAccount(account);
//        } else if ("SENDER".equalsIgnoreCase(type)) {
//            transactions = transactionRepository.findBySenderAccount(account);
//        } else {
//            // Handle invalid type value
//            throw new IllegalArgumentException("Invalid type value: " + type);
//        }
//
//        double totalIncome=0.00;
//        double totalExpense=0.00;
//        for (Transaction transaction : transactions) {
//            if ("DEPOSIT".equalsIgnoreCase(transaction.getTransactionType())) {
//                totalIncome += transaction.getTransactionAmount();
//            }
//            else {
//                totalExpense += transaction.getTransactionAmount();
//            }
//        }
//        Map<String, Double> result = new HashMap<>();
//        result.put("totalIncome", totalIncome);
//        result.put("totalExpense", totalExpense);
//        return result;
//    }
//}