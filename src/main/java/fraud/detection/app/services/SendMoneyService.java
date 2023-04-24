package fraud.detection.app.services;

import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import fraud.detection.app.utils.Logs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SendMoneyService {
    public final Logs logs;
    private  final UserRepository userRepository;
    public  UniversalResponse response;
    public final HelperUtility helperUtility;
    public final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfig;
    private final TransactionRepository transactionRepository;
    public UniversalResponse sendMoney(SendMoneyDTO request){
        String Account= request.getSenderAccountNumber();
         System.out.println(Account);

        if (helperUtility.checkPin(request.getPin(), request.getSenderAccountNumber())){
            try{
                if (helperUtility.checkAccount(request.getReceiverAccountNumber())){
                    UniversalResponse response= UniversalResponse.builder()
                            .message("The Mission Customer does not exist")
                            .status("failed")
                            .data(request)
                            .build();
                    return  response;
                }
                else {
                    try{Account fromaccount=accountRepository.findByAccountNumber(request.getSenderAccountNumber());
                        double Balance= fromaccount.getAccountBalance();
                        double SendingAmount=request.getTransactionAmount();
                        System.out.println(Balance);
                        System.out.println(SendingAmount);

                        if(Balance> SendingAmount){
                            double FromBalance =Balance- SendingAmount;
                            double ToBalalnce=Balance+ SendingAmount;
                            System.out.println(FromBalance);
                            System.out.println(ToBalalnce);
                            try {
                                Account Toaccount = accountRepository.findByAccountNumber(request.getReceiverAccountNumber());
                                Toaccount.setAccountBalance(ToBalalnce);
                                accountRepository.save(Toaccount);
                                Account FromAccount = accountRepository.findByAccountNumber(request.getSenderAccountNumber());
                                FromAccount.setAccountBalance(FromBalance);
                                accountRepository.save(FromAccount);

                                //TODO: Implement code to Insert into transaction Table here
//
                                try {
                                    Transaction TransObj = new Transaction();
                                    var trans = TransObj.builder()
                                            .transactionAmount((float) request.getTransactionAmount())
                                            .transactionType("SEND MONEY")
                                            .ReferenceCode(helperUtility.getTransactionUniqueNumber())
                                            .senderAccount(request.getSenderAccountNumber())
                                            .receiverAccount(request.getReceiverAccountNumber())
                                            .Debited(request.getTransactionAmount())
                                            .Credited(request.getTransactionAmount())
                                            .Status("success")
                                            .build();
                                    transactionRepository.save(trans);
                                }
                                catch (Exception ex){
                                    logs.log(ex.getMessage());
                                    Transaction TransObj = new Transaction();
                                    var trans = TransObj.builder()
                                            .transactionAmount((float) request.getTransactionAmount())
                                            .transactionType("SEND MONEY")
                                            .ReferenceCode(helperUtility.getTransactionUniqueNumber())
                                            .senderAccount(request.getSenderAccountNumber())
                                            .receiverAccount(request.getReceiverAccountNumber())
                                            .Debited(request.getTransactionAmount())
                                            .Credited(request.getTransactionAmount())
                                            .Status("failed")
                                            .build();
                                    transactionRepository.save(trans);
                                }
//
//                                //Sending messages to the sender and receiver
//                                SendMoneyMessage sendMoneyMessage = new SendMoneyMessage();
//                                var smsRequest = sendMoneyMessage.builder().senderPhoneNumber(request.getSenderAccountNumber())
//                                                .receiverPhoneNumber(request.getReceiverAccountNumber())
//                                                .message(request.getTransactionAmount())
//                                                .build();
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
//                                                    .create();
//
//                                    twilioMessage = Message.creator(
//                                                    new PhoneNumber(smsRequest.getReceiverPhoneNumber()),
//                                                    new PhoneNumber(twilioConfig.getTrial_number()),
//                                                    "You have Sent Ksh:" + request.
//                                                            getTransactionAmount() + "To"
//                                                            + request.getReceiverAccountNumber()
//                                                            + "You new Account Balance is Ksh:" + FromBalance)
//                                                    .create();
//                                }
//                                catch (Exception ex) {
//                                    System.out.println("Error While Sending Transaction Message" + ex);
//                                    UniversalResponse response = UniversalResponse.builder()
//                                            .message("Error While Sending Transaction Message")
//                                            .status(0)
//                                            .build();
//                                    return response;
//                                }
                            }
                            catch(Exception ex){
                                System.out.println("Transaction Error"+ex);
                                UniversalResponse response= UniversalResponse.builder()
                                        .message("Transaction Error")
                                        .status("failed")
                                        .data(request)
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
                                    .transactionAmount((float) request.getTransactionAmount())
                                    .transactionType("sendMoney")
                                    .ReferenceCode(referenceCode)
                                    .senderAccount(request.getSenderAccountNumber())
                                    .receiverAccount(request.getReceiverAccountNumber())
                                    .Debited(request.getTransactionAmount())
                                    .Credited(request.getTransactionAmount())
                                    .Status("failed not enough balance")
                                    .build();
                            transactionRepository.save(trans);

                            UniversalResponse response= UniversalResponse.builder()
                                    .message("Insufficient funds")
                                    .status("failed")
                                    .data(request)
                                    .build();
                            return  response;
                        }
                    }
                    catch (Exception ex){
                        logs.log(ex.getMessage());
                    }

                }
            }
            catch (Exception ex){
                Transaction TransObj = new Transaction();
                var trans = TransObj.builder()
                        .transactionAmount((float) request.getTransactionAmount())
                        .transactionType("sendMoney")
                        .ReferenceCode(helperUtility.getTransactionUniqueNumber())
                        .senderAccount(request.getSenderAccountNumber())
                        .receiverAccount(request.getReceiverAccountNumber())
                        .Debited(request.getTransactionAmount())
                        .Credited(request.getTransactionAmount())
                        .Status("failed")
                        .build();
                System.out.println("Transaction Failed"+ex);
                UniversalResponse response= UniversalResponse.builder()
                        .message("Transaction Failed")
                        .status("failed")
                        .data(request)
                        .build();
                return  response;
            }
        }
        else {
            UniversalResponse response= UniversalResponse.builder()
                    .message("You Entered The wrong Pin!")
                    .status("failed")
                    .data(request)
                    .build();
            return  response;
        }

        return response;
    }
}
