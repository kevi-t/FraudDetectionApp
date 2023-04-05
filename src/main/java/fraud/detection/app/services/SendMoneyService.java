package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfig;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.dto.SendMoneyMessage;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SendMoneyService {
    String  referenceCode;
    private final PasswordEncoder passwordEncoder;
    private  final UserRepository userRepository;
    public  UniversalResponse response;
    public final AccountRepository accountRepository;
    private final TwilioConfig twilioConfig;
    private final TransactionRepository transactionRepository;
    public UniversalResponse sendMoney(SendMoneyDTO request){
         String Upin= request.getPin();
         System.out.println(Upin);
         User user= userRepository.findUserBymobileNumber(request.getFrom());
        String Dbpin= user.getPin();
        if (passwordEncoder.matches(Upin, Dbpin)){
            try{
                if (userRepository.findUserByMobileNumber(request.getTo())==null){
                    UniversalResponse response= UniversalResponse.builder()
                            .message("The Mission Customer does not exist")
                            .status(0)
                            .build();
                    return  response;
                }
                else {
                    try{
                        Account fromaccount=accountRepository.findByaccountNo(request.getFrom());
                        double Balance= fromaccount.getBalance();
                        double SendingAmount=request.getAmount();
                        System.out.println(Balance);
                        System.out.println(SendingAmount);

                        if(Balance> SendingAmount){
                            double FromBalance =Balance- SendingAmount;
                            double ToBalalnce=Balance+ SendingAmount;
                            System.out.println(FromBalance);
                            System.out.println(ToBalalnce);
                            try {
                                Account Toaccount = accountRepository.findByaccountNo(request.getTo());
                                Toaccount.setBalance(ToBalalnce);
                                accountRepository.save(Toaccount);
                                Account FromAccount = accountRepository.findByaccountNo(request.getFrom());
                                FromAccount.setBalance(FromBalance);
                                accountRepository.save(FromAccount);
                                //generating unique reference code
                                UUID uuid = UUID.randomUUID();
                                String randomUUIDString = uuid.toString();
                                String referenceCode = "TUCN" + randomUUIDString;

                                //TODO: Implement code to Insert into transaction Table here

                                try {
                                    Transaction TransObj = new Transaction();
                                    var trans = TransObj.builder()
                                            .AMOUNT((float) request.getAmount())
                                            .Transaction_Type("sendMoney")
                                            .ReferenceCode(referenceCode)
                                            .sender(request.getFrom())
                                            .receiver(request.getTo())
                                            .Debited(request.getFrom())
                                            .Credited(request.getTo())
                                            .Status("0")
                                            .build();
                                    transactionRepository.save(trans);
                                }catch (Exception ex){
                                    Transaction TransObj = new Transaction();
                                    var trans = TransObj.builder()
                                            .AMOUNT((float) request.getAmount())
                                            .Transaction_Type("sendMoney")
                                            .ReferenceCode("Reference"+referenceCode)
                                            .sender(request.getFrom())
                                            .receiver(request.getTo())
                                            .Debited(request.getFrom())
                                            .Credited(request.getTo())
                                            .Status("1")
                                            .build();
                                    transactionRepository.save(trans);

                                }
                                //Sending messages to the sender and receiver
                                SendMoneyMessage sendMoneyMessage = new SendMoneyMessage();
                                var smsRequest = sendMoneyMessage.builder()
                                        .FromphoneNumber(request.getFrom()).TophoneNumber(request
                                                .getTo()).message(request.getAmount())
                                        .build();
                                System.out.println(smsRequest);
                                try {
                                    Message twilioMessage = Message.creator(
                                                    new PhoneNumber(smsRequest.getTophoneNumber()),
                                                    new PhoneNumber(twilioConfig.getTrial_number()),
                                                    "You have recieved Ksh:"
                                                            + request.getAmount() + "From"
                                                            + request.getFrom() + "You new Account Balance is Ksh:"
                                                            + ToBalalnce)
                                            .create();

                                    twilioMessage = Message.creator(
                                                    new PhoneNumber(smsRequest.getTophoneNumber()),
                                                    new PhoneNumber(twilioConfig.getTrial_number()),
                                                    "You have Sent Ksh:" + request.
                                                            getAmount() + "To" + request.getTo()
                                                            + "You new Account Balance is Ksh:" + FromBalance)
                                            .create();
                                } catch (Exception ex) {
                                    System.out.println("Error While Sending Transaction Message" + ex);
                                    UniversalResponse response = UniversalResponse.builder()
                                            .message("Error While Sending Transaction Message")
                                            .status(0)
                                            .build();
                                    return response;
                                }
                            }catch(Exception ex){
                                System.out.println("Transaction Error"+ex);
                                UniversalResponse response= UniversalResponse.builder()
                                        .message("Transaction Error")
                                        .status(0)
                                        .build();
                                return  response;
                            }
                        }
                        else{
                            UUID uuid = UUID.randomUUID();
                            String randomUUIDString = uuid.toString();
                            String referenceCode = "TUCN" + randomUUIDString;
                            Transaction TransObj = new Transaction();
                            var trans = TransObj.builder()
                                    .AMOUNT((float) request.getAmount())
                                    .Transaction_Type("sendMoney")
                                    .ReferenceCode(referenceCode)
                                    .sender(request.getFrom())
                                    .receiver(request.getTo())
                                    .Debited(request.getFrom())
                                    .Credited(request.getTo())
                                    .Status("1")
                                    .build();
                            transactionRepository.save(trans);
                            System.out.println("Insufficient funds");
                            UniversalResponse response= UniversalResponse.builder()
                                    .message("Insufficient funds")
                                    .status(0)
                                    .build();
                            return  response;
                        }
                    }catch (Exception ex){
                        System.out.println(ex);
                    }

                }
            }
            catch (Exception ex){
                Transaction TransObj = new Transaction();
                var trans = TransObj.builder()
                        .AMOUNT((float) request.getAmount())
                        .Transaction_Type("sendMoney")
                        .ReferenceCode(referenceCode)
                        .sender(request.getFrom())
                        .receiver(request.getTo())
                        .Debited(request.getFrom())
                        .Credited(request.getTo())
                        .Status("1")
                        .build();
                System.out.println("Transaction Failed"+ex);
                UniversalResponse response= UniversalResponse.builder()
                        .message("Transaction Failed")
                        .status(0)
                        .build();
                return  response;
            }
        }
        else {

            System.out.println("You Entered The wrong Pin");
            UniversalResponse response= UniversalResponse.builder()
                    .message("You Entered The wrong Pin!")
                    .status(0)
                    .build();
            return  response;
        }


        return response;
    }
}
