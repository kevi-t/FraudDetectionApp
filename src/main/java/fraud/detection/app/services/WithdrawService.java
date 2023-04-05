package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfig;
import fraud.detection.app.dto.WithdrawDTO;
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
public class WithdrawService {

    private final TwilioConfig twilioConfig;
    private  final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public  UniversalResponse response;

    @Autowired
    public WithdrawService(TwilioConfig twilioConfig, AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.twilioConfig = twilioConfig;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }



    public UniversalResponse withdrawMoney(WithdrawDTO request) {
        try{
            Account accountNumber2 = accountRepository.findByaccountNo(request.getAccountNumber());
            if (accountNumber2 == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
                double inputAmount = request.getTransactionAmount();
                double currentBalance = accountNumber2.getBalance();
                if (inputAmount >= currentBalance){
                    return UniversalResponse.builder().message("Insufficient Funds  Balance: "+currentBalance).build();
                }
                else {
                    double newAccountBalance = (currentBalance-inputAmount);
                    accountNumber2.setBalance(newAccountBalance);
                    accountRepository.save(accountNumber2);

                    Transaction transaction = Transaction.builder().sender(request.getAccountNumber())
                            .receiver(request.getAccountNumber())
                            .AMOUNT((float) request.getTransactionAmount())
                            .Transaction_Type("WITHDRAW").build();
                    transactionRepository.save(transaction);
                }
                return  UniversalResponse.builder().message("Withdraw Request Successful Account Balance: "+accountNumber2.
                        getBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }


}
