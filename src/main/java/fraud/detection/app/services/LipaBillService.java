package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.LipaBillDto;
import fraud.detection.app.dto.LipaBillResponse;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import fraud.detection.app.utils.LogFileCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@Service
@Slf4j
public class LipaBillService {

    private final TransactionRepository transactionRepository;
    private final LogFileCreator logFileCreator;
    private final HelperUtility helperUtility;
    private final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfiguration;
    private UniversalResponse response;
    private final LipaBillResponse lipaBillResponse;
    String referenceCode = HelperUtility.referenceCodeGenerator();

    public LipaBillService(TransactionRepository transactionRepository
            , LogFileCreator logFileCreator
            , HelperUtility helperUtility
            , AccountRepository accountRepository
            , TwilioConfiguration twilioConfiguration, LipaBillResponse lipaBillResponse) {
        this.transactionRepository = transactionRepository;
        this.logFileCreator = logFileCreator;
        this.helperUtility = helperUtility;
        this.accountRepository = accountRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.lipaBillResponse = lipaBillResponse;
    }

    public UniversalResponse lipaBill(LipaBillDto request) {
        String checkedAccountNumber = checkPhoneNumber(request.getPayerNo());

        Account account=accountRepository.findByAccountNumber(checkedAccountNumber);
        System.out.println(account);

        if (helperUtility.checkPin(request.getPin(),checkedAccountNumber)) {

            if (helperUtility.checkAccountBalance(request.getPayerNo(), request.getAmount())) {

                account = accountRepository.findByAccountNumber(checkedAccountNumber);
                double updatedAccountBalance = account.getAccountBalance() - request.getAmount();
                //updating Accounts Table
                double BeforeAccountBalance = account.getAccountBalance();
                account.setBalanceBefore(BeforeAccountBalance);
                account.setAccountBalance(updatedAccountBalance);
                accountRepository.save(account);

                //Inserting Into transaction Table
                Transaction trans = Transaction.builder()
                        .transactionType("LIPABILL")
                        .senderAccount(checkedAccountNumber)
                        .receiverAccount(request.getPayBillNo())
                        .status("success")
                        .transactionAmount(request.getAmount())
                        .ReferenceCode(referenceCode)
                        .build();
                transactionRepository.save(trans);


                //sending message to the payee
                try {
                    Message.creator(
                            new PhoneNumber(request.getPayerNo()),
                            new PhoneNumber(twilioConfiguration.getTrial_number()),
                            referenceCode+" Confirmed you have Payed Ksh:" + request.getAmount() + "To Paybill No:"+ request.getPayBillNo()
                            +"You new Account Balance is Ksh:" + updatedAccountBalance)
                            .create();
                }
                catch (Exception ex) {
                    System.out.println("Error While Sending Transaction Message" + ex);
                    log.info("Error While Sending Transaction Message ==>" + ex);
                }
                lipaBillResponse.setAmount(request.getAmount());
                lipaBillResponse.setRecievedBy(request.getPayBillNo());
                lipaBillResponse.setBalance(account.getAccountBalance());
                return  UniversalResponse.builder()
                        .message("Transaction Successful")
                        .status("1")
                        .data(lipaBillResponse)
                        .build();
            }
            else {

                //Inserting Into transaction Table
                Transaction trans = Transaction.builder()
                        .transactionType("LIPABILL")
                        .senderAccount(checkedAccountNumber)
                        .receiverAccount(request.getPayBillNo())
                        .status("failed")
                        .transactionAmount(request.getAmount())
                        .ReferenceCode(referenceCode)
                        .build();
                transactionRepository.save(trans);

                return UniversalResponse.builder()
                        .message("Transaction failed insufficient funds")
                        .status("1")
                        .build();
            }

        }
        else {

            return UniversalResponse.builder()
                    .message("Wrong pin")
                    .status("1")
                    .build();
        }
    }

}