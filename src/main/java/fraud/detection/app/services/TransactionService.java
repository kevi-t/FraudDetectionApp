package fraud.detection.app.services;

import fraud.detection.app.dto.DepositDTO;
import fraud.detection.app.dto.SendMoneyDTO;
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
public class TransactionService {

    private  final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public  UniversalResponse response;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public UniversalResponse depositMoney(DepositDTO request){
        try{
            Account accountNumber2 = accountRepository.findByAccountNumber(request.getAccountNumber());
            if (accountNumber2 == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
                double inputAmount = request.getTransactionAmount();
                double currentBalance = accountNumber2.getAccountBalance();
                double newAccountBalance = (inputAmount+currentBalance);

                accountNumber2.setAccountBalance(newAccountBalance);
                accountRepository.save(accountNumber2);

                Transaction transaction = Transaction.builder().accountNumber(request.getAccountNumber()).transactionAmount(request.getTransactionAmount()).build();
                transactionRepository.save(transaction);

               return  UniversalResponse.builder().message("Deposit Request Successful").balance(accountNumber2.getAccountBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public UniversalResponse withdrawMoney(WithdrawDTO request) {
        try{
            Account accountNumber2 = accountRepository.findByAccountNumber(request.getAccountNumber());
            if (accountNumber2 == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
                double inputAmount = request.getTransactionAmount();
                double currentBalance = accountNumber2.getAccountBalance();
                if (inputAmount >= currentBalance){
                    return UniversalResponse.builder().message("Insufficient Account Balance ").balance(currentBalance).build();
                }
                else {
                    double newAccountBalance = (currentBalance-inputAmount);
                    accountNumber2.setAccountBalance(newAccountBalance);
                    accountRepository.save(accountNumber2);

                    Transaction transaction = Transaction.builder().accountNumber(request.getAccountNumber()).transactionAmount(request.getTransactionAmount()).build();
                    transactionRepository.save(transaction);
                }
                return  UniversalResponse.builder().message("Withdraw Request Successful").balance(accountNumber2.getAccountBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public UniversalResponse sendMoney(SendMoneyDTO request) {
        String toAccountNo = request.getReceiverAccountNumber();

        try{
            Account fromAccountNumber = accountRepository.findByAccountNumber(request.getSenderAccountNumber());
            Account toAccountNumber = accountRepository.findByAccountNumber(request.getReceiverAccountNumber());

            if (fromAccountNumber == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else if (toAccountNumber == null){
                return UniversalResponse.builder().message("Sender Account not found, Please try Again").build();
            }
            else{
                double inputAmount = request.getTransactionAmount();
                double currentBalance = fromAccountNumber.getAccountBalance();
                if (inputAmount >= currentBalance){
                    return UniversalResponse.builder().message("Insufficient Account Balance ").balance(currentBalance).build();
                }
                double newAccountBalance = (currentBalance-inputAmount);
                fromAccountNumber.setAccountBalance(newAccountBalance);
                accountRepository.save(fromAccountNumber);

                double toNewAccountBalance = (currentBalance+inputAmount);
                toAccountNumber.setAccountBalance(toNewAccountBalance);
                accountRepository.save(toAccountNumber);

                Transaction transaction = Transaction.builder().accountNumber(request.getSenderAccountNumber()).transactionAmount(request.getTransactionAmount()).build();
                transactionRepository.save(transaction);

                Transaction transaction2 = Transaction.builder().accountNumber(request.getReceiverAccountNumber()).transactionAmount(request.getTransactionAmount()).build();
                transactionRepository.save(transaction2);

                return  UniversalResponse.builder().message("Send Money Request to Account: "+toAccountNo+" Successful").balance(fromAccountNumber.getAccountBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}