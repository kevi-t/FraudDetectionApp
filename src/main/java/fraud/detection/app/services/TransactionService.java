package fraud.detection.app.services;

import fraud.detection.app.dto.StatementDTO;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public UniversalResponse getIncomeAndExpenses(StatementDTO request) {
        try {
            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {

                try {
                    String account = request.getAccountNumber();
                    List<Transaction> transactions = transactionRepository.findBySenderAccountAndStatusOrReceiverAccountAndStatus(account,"success", account,"success");

                    double totalIncome = 0.00;
                    double totalExpense = 0.00;
                    for (Transaction transaction : transactions) {
                        if ("DEPOSIT".equalsIgnoreCase(transaction.getTransactionType())) {
                            totalIncome += transaction.getTransactionAmount();
                        }
                        else if ("SENDMONEY".equalsIgnoreCase(transaction.getTransactionType())) {
                          if (account.equals(transaction.getReceiverAccount())){
                              totalIncome += transaction.getTransactionAmount();
                          }
                          else if (account.equals(transaction.getSenderAccount())){
                              totalExpense += transaction.getTransactionAmount();
                          }
                        }
                        else if ("WITHDRAW".equalsIgnoreCase(transaction.getTransactionType())) {
                            totalExpense += transaction.getTransactionAmount();
                        }
                        else if ("LIPABILL".equalsIgnoreCase(transaction.getTransactionType())) {
                            totalExpense += transaction.getTransactionAmount();
                        }

//                        else {
//                            totalExpense += transaction.getTransactionAmount();
//                        }
                    }

                    BigDecimal income = BigDecimal.valueOf(totalIncome).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal expense = BigDecimal.valueOf(totalExpense).setScale(2, BigDecimal.ROUND_HALF_UP);

                    Map<String, BigDecimal> result = new HashMap<>();
                    result.put("totalIncome", income);
                    result.put("totalExpense", expense);
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