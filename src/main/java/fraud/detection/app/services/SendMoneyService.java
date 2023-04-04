package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.SendMoneyDTO;
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
public class SendMoneyService {

    private final TwilioConfiguration twilioConfig;
    private  final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private  final UserRepository userRepository;
    String  referenceCode;
    public  UniversalResponse response;

    @Autowired
    public SendMoneyService(TwilioConfiguration twilioConfig, AccountRepository accountRepository, TransactionRepository transactionRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.twilioConfig = twilioConfig;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UniversalResponse sendMoney(SendMoneyDTO request) {

            String EnteredPin= request.getPin();
            System.out.println(EnteredPin);

            String toAccountNo = request.getReceiverAccountNumber();
            Account fromAccountNumber = accountRepository.findByAccountNumber(request.getSenderAccountNumber());
            Account toAccountNumber = accountRepository.findByAccountNumber(request.getReceiverAccountNumber());
            User user= userRepository.findUserByMobileNumber(request.getSenderAccountNumber());
            String DatabasePin= user.getPin();
            if (fromAccountNumber == null) {
               return UniversalResponse.builder().message("Account not found, Please Create new Account").build();
            }
            else if (passwordEncoder.matches(EnteredPin, DatabasePin)){
                try{

                    if (toAccountNumber == null){
                        return UniversalResponse.builder().message("Receiver Account not found, Please try Again").build();
                    }
                    else{
                        double inputAmount = request.getTransactionAmount();
                        double currentBalance = fromAccountNumber.getAccountBalance();
                        if (inputAmount >= currentBalance){
                            UUID uuid = UUID.randomUUID();
                            String randomUUIDString = uuid.toString();
                            String referenceCode = "TUCN" + randomUUIDString;
                            Transaction TransObj = new Transaction();
                            var trans = TransObj.builder()
                                    .transactionAmount((float) request.getTransactionAmount())
                                    .transactionType("SEND MONEY")
                                    .ReferenceCode(referenceCode)
                                    .senderAccount(request.getSenderAccountNumber())
                                    .receiverAccount(request.getReceiverAccountNumber())
                                    .Debited(request.getSenderAccountNumber())
                                    .Credited(request.getReceiverAccountNumber())
                                    .Status("1")
                                    .build();
                            transactionRepository.save(trans);
                            return UniversalResponse.builder().message("Insufficient Funds  Account Balance: "+currentBalance).build();
                        }
                        try{
                            double newAccountBalance = (currentBalance-inputAmount);
                            fromAccountNumber.setAccountBalance(newAccountBalance);
                            accountRepository.save(fromAccountNumber);
                            double toNewAccountBalance = (currentBalance+inputAmount);
                            toAccountNumber.setAccountBalance(toNewAccountBalance);
                            accountRepository.save(toAccountNumber);

                            //generating unique reference code
                            UUID uuid = UUID.randomUUID();
                            String randomUUIDString = uuid.toString();
                            String referenceCode = "TUCN" + randomUUIDString;

                            //TODO: Implement code to Insert into transaction Table here

                            try {
                                Transaction TransObj = new Transaction();
                                var trans = TransObj.builder()
                                        .transactionAmount((float) request.getTransactionAmount())
                                        .transactionType("SEND MONEY")
                                        .ReferenceCode(referenceCode)
                                        .senderAccount(request.getSenderAccountNumber())
                                        .receiverAccount(request.getReceiverAccountNumber())
                                        .Debited(request.getSenderAccountNumber())
                                        .Credited(request.getReceiverAccountNumber())
                                        .Status("0")
                                        .build();
                                transactionRepository.save(trans);
                            }
                            catch (Exception ex){
                                Transaction TransObj = new Transaction();
                                var trans = TransObj.builder()
                                        .transactionAmount((float) request.getTransactionAmount())
                                        .transactionType("SEND MONEY")
                                        .ReferenceCode(referenceCode)
                                        .senderAccount(request.getSenderAccountNumber())
                                        .receiverAccount(request.getReceiverAccountNumber())
                                        .Debited(request.getSenderAccountNumber())
                                        .Credited(request.getReceiverAccountNumber())
                                        .Status("1")
                                        .build();
                                transactionRepository.save(trans);

                            }

                            //Sending messages to the Sender and Receiver

                            var smsRequest= SendMoneyMessage.builder()
                                    .senderPhoneNumber(request.getSenderAccountNumber())
                                    .receiverPhoneNumber(request.getReceiverAccountNumber())
                                    .message(request.getTransactionAmount())
                                    .build();
                            System.out.println(smsRequest);
                            try {
                                 Message twilioMessage = Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
                                         new PhoneNumber(twilioConfig.getTrial_number()), "You have received. Ksh"+request.getTransactionAmount()+" From"+request.getSenderAccountNumber()).create();
                                 twilioMessage = Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
                                         new PhoneNumber(twilioConfig.getTrial_number()),"You have Sent. Ksh"+request.getTransactionAmount()+" To"+request.getReceiverAccountNumber()).create();
                            }
                            catch (Exception ex) {
                                 System.out.println("Error While Sending Transaction Message"+ex);
                                 return  UniversalResponse.builder().message("Error While Sending Transaction Message").status(0).build();
                            }
                        return  UniversalResponse.builder().message("Send Money Request to Account: "+toAccountNo+" Successful New Account Balance: "+fromAccountNumber.getAccountBalance()).build();
                        }
                        catch (Exception e){
                            return  UniversalResponse.builder().message("Transaction Error").status(0).build();
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Transaction TransObj = new Transaction();
                    var trans = TransObj.builder()
                            .transactionAmount((float) request.getTransactionAmount())
                            .transactionType("SEND MONEY")
                            .ReferenceCode(referenceCode)
                            .senderAccount(request.getSenderAccountNumber())
                            .receiverAccount(request.getReceiverAccountNumber())
                            .Debited(request.getSenderAccountNumber())
                            .Credited(request.getReceiverAccountNumber())
                            .Status("1")
                            .build();
                    transactionRepository.save(trans);
                    return  UniversalResponse.builder().message("Transaction Failed").status(0).build();
                }
            }
            else{
                System.out.println("You Entered The wrong Pin");
                UniversalResponse response= UniversalResponse.builder().message("You Entered The wrong Pin!").status(0).build();
                return  response;
            }

    }
}