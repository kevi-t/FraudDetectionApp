package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.LipaBillDto;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LipaBillService {
    private final HelperUtility helperUtility;
    private final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfiguration;
private UniversalResponse response;

    public LipaBillService(HelperUtility helperUtility
            , AccountRepository accountRepository
            , TwilioConfiguration twilioConfiguration) {
        this.helperUtility = helperUtility;
        this.accountRepository = accountRepository;
        this.twilioConfiguration = twilioConfiguration;
    }

    public UniversalResponse lipaBill(LipaBillDto request) {

        System.out.println("Payeeerr"+request.getPayerNo());
        Account account=accountRepository.findByAccountNumber(request.getPayerNo());
        System.out.println(account);
        if (helperUtility.checkPin(request.getPin(),request.getPayerNo())) {
            if (helperUtility.checkAccountBalance(request.getPayerNo(), request.getAmount())) {
                 account = accountRepository.findByAccountNumber(request.getPayerNo());
                double updatedAccountBalance = account.getAccountBalance() - request.getAmount();
                //updating Accounts Table
                double BeforeAccountBalance = account.getAccountBalance();
                account.setBalanceBefore(BeforeAccountBalance);
                account.setAccountBalance(updatedAccountBalance);
                accountRepository.save(account);
                //Inserting Into transaction Table
                Transaction transaction = new Transaction();
                var transObj = transaction.builder()
                        .Debited(request.getAmount())
                        .Credited(request.getAmount())
                        .transactionType("LipaBill")
                        .senderAccount(request.getPayerNo())
                        .receiverAccount(request.getPayBillNo())
                        .Status("completed")
                        .transactionAmount(request.getAmount())
                        .ReferenceCode(helperUtility.getTransactionUniqueNumber()).
                        build();
                //sending message to the payee
                try {
                                                 Message.creator(
                                                    new PhoneNumber(request.getPayerNo()),
                                                    new PhoneNumber(twilioConfiguration.getTrial_number()),
                                                    "You have Payed Ksh:" + request.
                                                            getAmount() + "To Paybill No:"
                                                            + request.getPayBillNo()
                                                            + "You new Account Balance is Ksh:" + updatedAccountBalance)
                                                    .create();
                                }
                                catch (Exception ex) {
                                    System.out.println("Error While Sending Transaction Message" + ex);
log.info("Error While Sending Transaction Message ==>" + ex);
                                }
            } else {
                UniversalResponse response= UniversalResponse.builder()
                        .message("You have Insufficient Balance")
                        .status("failed")
                        .data(request)
                        .build();
                return  response;
            }

        } else {
            //TODO return Entered wrong pin
            UniversalResponse response= UniversalResponse.builder()
                    .message("You have Enetred the wrong Pin")
                    .status("failed")
                    .data(request)
                    .build();
            return  response;
        }
        return response;
    }

}
