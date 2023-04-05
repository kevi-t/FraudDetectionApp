package fraud.detection.app.repositories;


import fraud.detection.app.models.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {
        Account findByaccountNo(String account);
       // String findByuser(String account);
    }

