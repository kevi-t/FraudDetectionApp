package fraud.detection.app.services;

import fraud.detection.app.dto.AccountDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {

    private  final AccountRepository accountRepository;
    public  UniversalResponse response;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public UniversalResponse checkBalance(AccountDTO request){
        try{
            Account accountNumber = accountRepository.findByAccountNumber(request.getAccountNumber());
            if (accountNumber == null) {
                return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else{
                return  UniversalResponse.builder().message("Balance Request Successful").balance(accountNumber.getAccountBalance()).build();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}