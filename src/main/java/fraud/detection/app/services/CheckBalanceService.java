package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.CheckBalanceDTO;
import fraud.detection.app.dto.SendMoneyMessage;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckBalanceService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private  final TwilioConfiguration twilioConfiguration;
    public final HelperUtility helperUtility;
    public UniversalResponse response;
    String referenceCode = HelperUtility.referenceCodeGenerator();

    @Autowired
    public CheckBalanceService(AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               TwilioConfiguration twilioConfiguration, HelperUtility helperUtility) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse checkBalance(CheckBalanceDTO request) {
        try {
              Account accountNumber = accountRepository.findByAccountNumber(request.getAccountNumber());
            if (helperUtility.checkPin(request.getPin(),request.getAccountNumber())){
                try{

                    Transaction transaction = Transaction.builder()
                            .ReferenceCode(referenceCode)
                            .transactionType("CHECK BALANCE")
                            .Status("0")
                            .build();
                    transactionRepository.save(transaction);

                    //Sending messages to the Sender and Receiver
                    var smsRequest = SendMoneyMessage.builder()
                            .senderPhoneNumber(request.getAccountNumber())
                            .receiverPhoneNumber(request.getAccountNumber())
                            .message(accountNumber.getAccountBalance())
                            .build();
                    System.out.println(smsRequest);
                    try {
                        Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
                                new PhoneNumber(twilioConfiguration.getTrial_number()), "Account balance request. Ksh" + accountNumber.getAccountBalance())
                                .create();

                        return UniversalResponse.builder().message("Balance request successful account balance:" + accountNumber.getAccountBalance()).build();
                    }
                    catch (Exception ex) {
                        System.out.println("Error While Sending Transaction Message" + ex);
                        return UniversalResponse.builder().message("Error While Sending Transaction Message").status(0).build();
                    }

                }
                catch (Exception ex){
                    return UniversalResponse.builder().message("Transaction Error").status(0).build();
                }
            }
            else {
                return UniversalResponse.builder().message("Wrong Pin!").status(0).build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}