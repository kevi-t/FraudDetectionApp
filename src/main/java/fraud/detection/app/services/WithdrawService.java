package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.WithdrawDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import fraud.detection.app.utils.Logs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WithdrawService {
    public final Logs logs;
    private final TwilioConfiguration twilioConfig;
    private  final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public  UniversalResponse response;
    public final HelperUtility helperUtility;
    String referenceCode = HelperUtility.referenceCodeGenerator();

    @Autowired
    public WithdrawService(Logs logs, TwilioConfiguration twilioConfig
            , AccountRepository accountRepository
            , TransactionRepository transactionRepository
            , HelperUtility helperUtility) {
        this.logs = logs;
        this.twilioConfig = twilioConfig;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse withdrawMoney(WithdrawDTO request) {

        try{
            Account accountNumber = accountRepository.findByAccountNumber(request.getAccountNumber());

            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())) {

                double inputAmount = request.getTransactionAmount();
                double currentBalance = accountNumber.getAccountBalance();

                if (inputAmount >= currentBalance){
                    var trans = Transaction.builder()
                            .transactionAmount(request.getTransactionAmount())
                            .transactionType("WITHDRAW")
                            .ReferenceCode(referenceCode)
                            .senderAccount(request.getAccountNumber())
                            .receiverAccount(request.getAccountNumber())
                            .status("failed")
                            .build();
                    transactionRepository.save(trans);

                    return UniversalResponse.builder()
                            .message("Transaction failed insufficient funds")
                            .data(currentBalance)
                            .status("1")
                            .build();
                }
                else {

                    try{
                        double newAccountBalance = (currentBalance-inputAmount);
                        accountNumber.setAccountBalance(newAccountBalance);
                        accountRepository.save(accountNumber);

                        Transaction transaction = Transaction.builder()
                                .senderAccount(request.getAccountNumber())
                                .receiverAccount(request.getAccountNumber())
                                .transactionAmount(request.getTransactionAmount())
                                .ReferenceCode(referenceCode)
                                .transactionType("WITHDRAW")
                                .status("success")
                                .build();
                        transactionRepository.save(transaction);

                        try {
                            Message.creator(
                                    new PhoneNumber(request.getAccountNumber()),
                                    new PhoneNumber(twilioConfig.getTrial_number()),
                                    "Confirmed Withdraw. Ksh" + request.getTransactionAmount()+"Account balance.Ksh"+accountNumber.getAccountBalance())
                                    .create();

                            return  UniversalResponse.builder()
                                    .message(" Withdraw request successful")
                                    .status("0")
                                    .data("Amount:"+request.getTransactionAmount())
                                    .build();
                        }
                        catch (Exception ex) {
                            logs.log("Error While Sending Transaction Message===>" +ex.getMessage());
                            return UniversalResponse.builder()
                                    .message("Error while sending transaction message")
                                    .status("1")
                                    .build();
                        }
                    }
                    catch (Exception ex){
                        logs.log(ex.getMessage());
                        ex.printStackTrace();
                        var trans = Transaction.builder()
                                .transactionAmount(request.getTransactionAmount())
                                .transactionType("WITHDRAW")
                                .ReferenceCode(referenceCode)
                                .senderAccount(request.getAccountNumber())
                                .receiverAccount(request.getAccountNumber())
                                .status("failed")
                                .build();
                        transactionRepository.save(trans);

                        return UniversalResponse.builder()
                                .message("Transaction failed")
                                .status("1")
                                .build();
                    }
                }
            }
            else{
                return UniversalResponse.builder()
                        .message("Wrong pin!")
                        .status("1")
                        .build();
            }
        }
        catch (Exception e){
           logs.log(e.getMessage());
        }
        return response;
    }
}