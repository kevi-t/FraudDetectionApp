//package fraud.detection.app.services;
//
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import fraud.detection.app.configurations.TwilioConfiguration;
//import fraud.detection.app.dto.SendMoneyDTO;
//import fraud.detection.app.dto.SendMoneyMessage;
//import fraud.detection.app.models.Account;
//import fraud.detection.app.models.Transaction;
//import fraud.detection.app.models.User;
//import fraud.detection.app.repositories.AccountRepository;
//import fraud.detection.app.repositories.TransactionRepository;
//import fraud.detection.app.repositories.UserRepository;
//import fraud.detection.app.responses.UniversalResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@RequiredArgsConstructor
//@Service
//public class SendMoneyService {
//    String  referenceCode;
//    private final PasswordEncoder passwordEncoder;
//    private  final UserRepository userRepository;
//    public  UniversalResponse response;
//    public final AccountCheck accountCheck;
//    public final AccountRepository accountRepository;
//    private final TwilioConfiguration twilioConfig;
//    private final PinCheck pinCheck;
//    private final TransactionRepository transactionRepository;
//    public UniversalResponse sendMoney(SendMoneyDTO request){
//         String Upin= request.getPin();
//        // System.out.println(Upin);
//        String Account= request.getSenderAccountNumber();
//         System.out.println(Account);
//             User user= userRepository.findUserBymobileNumber(Account);
//             System.out.println(user.getPin());
//
//         System.out.println(user);
//        String Dbpin= user.getPin();
//        System.out.println(Dbpin);
//        if (pinCheck.checkPin(request.getPin(), request.getSenderAccountNumber())){
//            try{
//                if (accountCheck.checkAccount(request.getReceiverAccountNumber())){
//                    UniversalResponse response= UniversalResponse.builder()
//                            .message("The Mission Customer does not exist")
//                            .status(0)
//                            .build();
//                    return  response;
//                }
//                else {
//                    try{Account fromaccount=accountRepository.findByAccountNumber(request.getSenderAccountNumber());
//                        double Balance= fromaccount.getAccountBalance();
//                        double SendingAmount=request.getTransactionAmount();
//                        System.out.println(Balance);
//                        System.out.println(SendingAmount);
//
//                        if(Balance> SendingAmount){
//                            double FromBalance =Balance- SendingAmount;
//                            double ToBalalnce=Balance+ SendingAmount;
//                            System.out.println(FromBalance);
//                            System.out.println(ToBalalnce);
//                            try {
//                                Account Toaccount = accountRepository
//                                        .findByAccountNumber(request.getReceiverAccountNumber());
//                                Toaccount.setAccountBalance(ToBalalnce);
//                                accountRepository.save(Toaccount);
//                                Account FromAccount = accountRepository
//                                        .findByAccountNumber(request.getSenderAccountNumber());
//                                FromAccount.setAccountBalance(FromBalance);
//                                accountRepository.save(FromAccount);
//                                //generating unique reference code
//                                UUID uuid = UUID.randomUUID();
//                                String randomUUIDString = uuid.toString();
//                                String referenceCode = "TUCN" + randomUUIDString;
//
//                                //TODO: Implement code to Insert into transaction Table here
//
//                                try {
//                                    Transaction TransObj = new Transaction();
//                                    var trans = TransObj.builder()
//                                            .transactionAmount((float) request.getTransactionAmount())
//                                            .transactionType("sendMoney")
//                                            .ReferenceCode(referenceCode)
//                                            .senderAccount(request.getSenderAccountNumber())
//                                            .receiverAccount(request.getReceiverAccountNumber())
//                                            .Debited(request.getSenderAccountNumber())
//                                            .Credited(request.getReceiverAccountNumber())
//                                            .Status("0")
//                                            .build();
//
//                                }catch (Exception ex){
//                                    Transaction TransObj = new Transaction();
//                                    var trans = TransObj.builder()
//                                            .transactionAmount((float) request.getTransactionAmount())
//                                            .transactionType("sendMoney")
//                                            .ReferenceCode(referenceCode)
//                                            .senderAccount(request.getSenderAccountNumber())
//                                            .receiverAccount(request.getReceiverAccountNumber())
//                                            .Debited(request.getSenderAccountNumber())
//                                            .Credited(request.getReceiverAccountNumber())
//                                            .Status("1")
//                                            .build();
//                                    transactionRepository.save(trans);
//
//                                }
//                                //Sending messages to the sender and receiver
//                                SendMoneyMessage sendMoneyMessage = new SendMoneyMessage();
//                                var smsRequest = sendMoneyMessage.builder()
//                                        .senderPhoneNumber(request.getSenderAccountNumber())
//                                        .receiverPhoneNumber(request
//                                                .getReceiverAccountNumber())
//                                        .message(request.getTransactionAmount())
//                                        .build();
//                                System.out.println(smsRequest);
//                                try {
//                                    Message twilioMessage = Message.creator(
//                                                    new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
//                                                    new PhoneNumber(twilioConfig.getTrial_number()),
//                                                    "You have recieved Ksh:"
//                                                            + request.getTransactionAmount() + "From"
//                                                            + request.getSenderAccountNumber()
//                                                            + "You new Account Balance is Ksh:"
//                                                            + ToBalalnce)
//                                            .create();
//
//                                    twilioMessage = Message.creator(
//                                                    new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
//                                                    new PhoneNumber(twilioConfig.getTrial_number()),
//                                                    "You have Sent Ksh:" + request.
//                                                            getTransactionAmount() + "To"
//                                                            + request.getReceiverAccountNumber()
//                                                            + "You new Account Balance is Ksh:" + FromBalance)
//                                            .create();
//                                } catch (Exception ex) {
//                                    System.out.println("Error While Sending Transaction Message" + ex);
//                                    UniversalResponse response = UniversalResponse.builder()
//                                            .message("Error While Sending Transaction Message")
//                                            .status(0)
//                                            .build();
//                                    return response;
//                                }
//                            }catch(Exception ex){
//                                System.out.println("Transaction Error"+ex);
//                                UniversalResponse response= UniversalResponse.builder()
//                                        .message("Transaction Error")
//                                        .status(0)
//                                        .build();
//                                return  response;
//                            }
//                        }
//                        else{
//                            UUID uuid = UUID.randomUUID();
//                            String randomUUIDString = uuid.toString();
//                            String referenceCode = "TUCN" + randomUUIDString;
//                            Transaction TransObj = new Transaction();
//                            var trans = TransObj.builder()
//                                    .transactionAmount((float) request.getTransactionAmount())
//                                    .transactionType("sendMoney")
//                                    .ReferenceCode(referenceCode)
//                                    .senderAccount(request.getSenderAccountNumber())
//                                    .receiverAccount(request.getReceiverAccountNumber())
//                                    .Debited(request.getSenderAccountNumber())
//                                    .Credited(request.getReceiverAccountNumber())
//                                    .Status("1")
//                                    .build();
//                            transactionRepository.save(trans);
//                            System.out.println("Insufficient funds");
//                            UniversalResponse response= UniversalResponse.builder()
//                                    .message("Insufficient funds")
//                                    .status(0)
//                                    .build();
//                            return  response;
//                        }
//                    }catch (Exception ex){
//                        System.out.println(ex);
//                    }
//
//                }
//            }
//            catch (Exception ex){
//                Transaction TransObj = new Transaction();
//                var trans = TransObj.builder()
//                        .transactionAmount((float) request.getTransactionAmount())
//                        .transactionType("sendMoney")
//                        .ReferenceCode(referenceCode)
//                        .senderAccount(request.getSenderAccountNumber())
//                        .receiverAccount(request.getReceiverAccountNumber())
//                        .Debited(request.getSenderAccountNumber())
//                        .Credited(request.getReceiverAccountNumber())
//                        .Status("1")
//                        .build();
//                System.out.println("Transaction Failed"+ex);
//                UniversalResponse response= UniversalResponse.builder()
//                        .message("Transaction Failed")
//                        .status(1)
//                        .build();
//                return  response;
//            }
//        }
//        else {
//
//            System.out.println("You Entered The wrong Pin");
//            UniversalResponse response= UniversalResponse.builder()
//                    .message("You Entered The wrong Pin!")
//                    .status(1)
//                    .build();
//            return  response;
//        }
//
//
//        return response;
//    }
//}
