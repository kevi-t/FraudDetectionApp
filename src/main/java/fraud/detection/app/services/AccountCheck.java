package fraud.detection.app.services;

import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class AccountCheck {
    private final AccountRepository accountRepository;
    public AccountCheck( AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Boolean checkAccount(String account){
if(accountRepository.findByAccountNumber(account)==null);
        return true;
    }
}
