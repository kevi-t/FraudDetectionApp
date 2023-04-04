package fraud.detection.app.services;

import fraud.detection.app.dto.CheckBalanceDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckBalanceService {

    private  final AccountRepository accountRepository;
    public  UniversalResponse response;

    @Autowired
    public CheckBalanceService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public UniversalResponse checkBalance(CheckBalanceDTO request){
        try{
            Account accountNumber = accountRepository.findByaccountNo(request.getAccountNumber());
            if (accountNumber == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
<<<<<<<< HEAD:src/main/java/fraud/detection/app/services/CheckBalanceService.java
                return  UniversalResponse.builder().message("Balance Request Successful |Account Balance:"+accountNumber.getAccountBalance()).build();
========
                return  UniversalResponse.builder().message("Balance Request Successful").balance(accountNumber.getBalance()).build();
>>>>>>>> 03a091269796c0ab07bf0a37fc5a96c0b396c65f:src/main/java/fraud/detection/app/services/AccountService.java
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}