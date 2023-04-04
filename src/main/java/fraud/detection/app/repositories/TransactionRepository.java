package fraud.detection.app.repositories;

import fraud.detection.app.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    //List<Transaction> findBySenderAccountOrReceiverOrSender(String senderAccount, String receiverAccount);
}