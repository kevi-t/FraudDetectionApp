//package fraud.detection.app.services;
//
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import fraud.detection.app.configurations.TwilioConfiguration;
//import fraud.detection.app.dto.SendMoneyMessage;
//import fraud.detection.app.dto.WithdrawDTO;
//import fraud.detection.app.models.Account;
//import fraud.detection.app.models.Transaction;
//import fraud.detection.app.models.User;
//import fraud.detection.app.repositories.AccountRepository;
//import fraud.detection.app.repositories.TransactionRepository;
//import fraud.detection.app.repositories.UserRepository;
//import fraud.detection.app.responses.UniversalResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//@Slf4j
//public class WithdrawService {
//
//    private final TwilioConfiguration twilioConfig;
//    private  final AccountRepository accountRepository;
//    private final TransactionRepository transactionRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    public  UniversalResponse response;
//
//    @Autowired
//    public WithdrawService(TwilioConfiguration twilioConfig, AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.twilioConfig = twilioConfig;
//        this.accountRepository = accountRepository;
//        this.transactionRepository = transactionRepository;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//    public UniversalResponse withdrawMoney(WithdrawDTO request) {
//
//        try{
//            Account accountNumber2 = accountRepository.findByAccountNumber(request.getAccountNumber());
//            String EnteredPin = request.getPin();
//            User user = userRepository.findUserBymobileNumber(request.getAccountNumber());
//            String DatabasePin = user.getPin();
//
//            if (accountNumber2 == null) {
//                return UniversalResponse.builder().message("Account not found, please try again").build();
//            }
//            else if (passwordEncoder.matches(EnteredPin, DatabasePin)) {
//                System.out.println("You Entered The wrong Pin");
//                return UniversalResponse.builder().message("You entered the wrong pin!").status(0).build();
//            }
//            else{
//                double inputAmount = request.getTransactionAmount();
//                double currentBalance = accountNumber2.getAccountBalance();
//                //generating unique reference code
//                UUID uuid = UUID.randomUUID();
//                String randomUUIDString = uuid.toString();
//                String referenceCode = "TUCN" + randomUUIDString;
//
//                if (inputAmount >= currentBalance){
//                    var trans = Transaction.builder()
//                            .transactionAmount((float) request.getTransactionAmount())
//                            .transactionType("WITHDRAW MONEY")
//                            .ReferenceCode(referenceCode)
//                            .senderAccount(request.getAccountNumber())
//                            .receiverAccount(request.getAccountNumber())
//                            .Debited(request.getAccountNumber())
//                            .Credited(request.getAccountNumber())
//                            .Status("1")
//                            .build();
//                    transactionRepository.save(trans);
//                    return UniversalResponse.builder().message("Transaction failed insufficient funds  balance: "+currentBalance).status(0).build();
//                }
//                else {
//
//                    try{
//                        double newAccountBalance = (currentBalance-inputAmount);
//                        accountNumber2.setAccountBalance(newAccountBalance);
//                        accountRepository.save(accountNumber2);
//
//                        Transaction transaction = Transaction.builder()
//                                .senderAccount(request.getAccountNumber())
//                                .receiverAccount(request.getAccountNumber())
//                                .transactionAmount(request.getTransactionAmount())
//                                .ReferenceCode(referenceCode)
//                                .transactionType("WITHDRAW")
//                                .Debited(request.getAccountNumber())
//                                .Credited(request.getAccountNumber())
//                                .Status("0")
//                                .build();
//                        transactionRepository.save(transaction);
//
//                        //Sending messages to the Sender and Receiver
//                        var smsRequest = SendMoneyMessage.builder()
//                                .senderPhoneNumber(request.getAccountNumber())
//                                .receiverPhoneNumber(request.getAccountNumber())
//                                .message(request.getTransactionAmount())
//                                .build();
//                        System.out.println(smsRequest);
//                        try {
//                            Message twilioMessage = Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
//                                    new PhoneNumber(twilioConfig.getTrial_number()), "You have received. Ksh" + request.getTransactionAmount() + " From" + request.getAccountNumber()).create();
//                            twilioMessage = Message.creator(new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
//                                    new PhoneNumber(twilioConfig.getTrial_number()), "You have Sent. Ksh" + request.getTransactionAmount() + " To" + request.getAccountNumber()).create();
//
//                            return  UniversalResponse.builder().message("Withdraw Request of Amount:"+request.getTransactionAmount()+"Successful new account Balance: "+accountNumber2.getAccountBalance()).build();
//                        }
//                        catch (Exception ex) {
//                            System.out.println("Error While Sending Transaction Message" + ex);
//                            return UniversalResponse.builder().message("Error While Sending Transaction Message").status(0).build();
//                        }
//                    }
//                    catch (Exception ex){
//                        ex.printStackTrace();
//                        var trans = Transaction.builder()
//                                .transactionAmount((float) request.getTransactionAmount())
//                                .transactionType("WITHDRAW MONEY")
//                                .ReferenceCode(referenceCode)
//                                .senderAccount(request.getAccountNumber())
//                                .receiverAccount(request.getAccountNumber())
//                                .Debited(request.getAccountNumber())
//                                .Credited(request.getAccountNumber())
//                                .Status("1")
//                                .build();
//                        transactionRepository.save(trans);
//                        return UniversalResponse.builder().message("Transaction Failed").status(0).build();
//                    }
//                }
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return response;
//    }
//}