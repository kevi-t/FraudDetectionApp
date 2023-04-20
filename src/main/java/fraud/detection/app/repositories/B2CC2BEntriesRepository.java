package fraud.detection.app.repositories;

import fraud.detection.app.models.B2C_C2B_Entries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface B2CC2BEntriesRepository extends JpaRepository<B2C_C2B_Entries, String> {

        // Find Record By ConversationID or OriginatorConversationID ...
        B2C_C2B_Entries findByConversationIdOrOriginatorConversationId(String conversationId, String originatorConversationId);

        // Find Transaction By TransactionId ....
        B2C_C2B_Entries findByTransactionId(String transactionId);

        B2C_C2B_Entries findByBillRefNumber(String billRefNumber);

}