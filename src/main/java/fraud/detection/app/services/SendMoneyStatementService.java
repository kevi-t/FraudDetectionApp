package fraud.detection.app.services;


import fraud.detection.app.dto.FilteredTransactions;
import fraud.detection.app.dto.StatementDTO;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@Service
@Slf4j
public class SendMoneyStatementService {

    private final TransactionRepository transactionRepository;
    public UniversalResponse response;
    public final HelperUtility helperUtility;

    @Autowired
    public SendMoneyStatementService(TransactionRepository transactionRepository, HelperUtility helperUtility) {
        this.transactionRepository = transactionRepository;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse getAllSendMoneyUserTransactions(StatementDTO request) {
        String checkedAccountNumber = checkPhoneNumber(request.getAccountNumber());
        try {

            if (helperUtility.checkPin(request.getPin(), checkedAccountNumber)) {

                try {

                    String account = checkedAccountNumber;
                    List<Transaction> transactions = transactionRepository.findBySenderAccountAndStatusAndTransactionTypeOrReceiverAccountAndStatusAndTransactionType
                            (account,"success","SENDMONEY",account,"success","SENDMONEY");

                    // Create a new list to store filtered transactions
                    List<FilteredTransactions> filteredTransactions = new ArrayList<>();
                    try {

                        for (Transaction transaction : transactions) {
                            FilteredTransactions filteredTransaction = new FilteredTransactions();
                            filteredTransaction.setTransactionType(transaction.getTransactionType());
                            filteredTransaction.setReceiverAccount(transaction.getReceiverAccount());
                            filteredTransaction.setReferenceCode(transaction.getReferenceCode());
                            filteredTransaction.setTransactionDate(transaction.getTransactionDate());
                            filteredTransaction.setTransactionAmount(transaction.getTransactionAmount());

                            // Add the filtered transaction to the filteredTransactions list
                            filteredTransactions.add(filteredTransaction);
                        }
                        return UniversalResponse.builder()
                                .data(filteredTransactions)
                                .status("1")
                                .message("request successful")
                                .build();
                    }
                    catch (Exception e) {
                        // throw new RuntimeException(e);
                        System.out.println(e);
                    }

                }
                catch (Exception e) {
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
