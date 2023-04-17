package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.CheckBalanceDTO;
import fraud.detection.app.dto.SendMoneyMessage;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class CheckBalanceService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private  final TwilioConfiguration twilioConfiguration;
    public UniversalResponse response;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CheckBalanceService(AccountRepository accountRepository, TransactionRepository transactionRepository, TwilioConfiguration twilioConfiguration, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UniversalResponse checkBalance(CheckBalanceDTO request) {
        try {
            Account accountNumber = accountRepository.findByAccountNumber(request.getAccountNumber());
            String EnteredPin = request.getPin();
            User user = userRepository.findUserBymobileNumber(request.getAccountNumber());
            String DatabasePin = user.getPin();
            if (accountNumber == null) {
                return UniversalResponse.builder().message("Account not found,please try again").build();
            }
            else if (passwordEncoder.matches(EnteredPin, DatabasePin)){
                System.out.println("You Entered The wrong Pin");
                return UniversalResponse.builder().message("You Entered The wrong Pin!").status(0).build();
            }
            else {
                try{
                    //generating unique reference code
                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();
                    String referenceCode = "TUCN" + randomUUIDString;

                    Transaction transaction = Transaction.builder()
                            .receiverAccount(request.getAccountNumber())
                            .senderAccount(request.getAccountNumber())
                            .transactionAmount(accountNumber.getAccountBalance())
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
                        Message twilioMessage = Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
                                new PhoneNumber(twilioConfiguration.getTrial_number()), "Account balance request. Ksh" + accountNumber.getAccountBalance()).create();

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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}