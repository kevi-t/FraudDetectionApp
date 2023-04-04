package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfig;
import fraud.detection.app.dto.DepositDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DepositService {

    private final TwilioConfig twilioConfig;
    private  final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public  UniversalResponse response;

    @Autowired
    public DepositService(TwilioConfig twilioConfig, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.twilioConfig = twilioConfig;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    public UniversalResponse depositMoney(DepositDTO request){
        try{
            Account accountNumber2 = accountRepository.findByaccountNo(request.getAccountNumber());
            if (accountNumber2 == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
                double inputAmount = request.getTransactionAmount();
                double currentBalance = accountNumber2.getBalance();
                double newAccountBalance = (inputAmount+currentBalance);

                accountNumber2.setBalance(newAccountBalance);
                accountRepository.save(accountNumber2);

                Transaction transaction = Transaction.builder().
                        sender(request.getAccountNumber()).
                        receiver(request.getAccountNumber()).
                        AMOUNT(request.getTransactionAmount()).Transaction_Type("DEPOSIT").build();
                transactionRepository.save(transaction);

                return  UniversalResponse.builder()
                        .message("Deposit Request Successful New Account Balance: "+ accountNumber2.getBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
