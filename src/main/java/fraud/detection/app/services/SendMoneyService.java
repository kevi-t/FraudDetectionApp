package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.SendMoneyDTO;
import fraud.detection.app.dto.SendMoneyResponseDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import fraud.detection.app.utils.Logs;
import org.springframework.stereotype.Service;

@Service
public class SendMoneyService {
    
    public final Logs logs;

    public  UniversalResponse response;
    public final HelperUtility helperUtility;
    public final AccountRepository accountRepository;
    private final TwilioConfiguration twilioConfig;
    private final TransactionRepository transactionRepository;
    private final SendMoneyResponseDTO sendMoneyResponseDTO;
    String referenceCode = HelperUtility.referenceCodeGenerator();

    public SendMoneyService(Logs logs,
                            HelperUtility helperUtility,
                            AccountRepository accountRepository,
                            TwilioConfiguration twilioConfig,
                            TransactionRepository transactionRepository, SendMoneyResponseDTO sendMoneyResponseDTO) {
        this.logs = logs;
        this.helperUtility = helperUtility;
        this.accountRepository = accountRepository;
        this.twilioConfig = twilioConfig;
        this.transactionRepository = transactionRepository;
        this.sendMoneyResponseDTO = sendMoneyResponseDTO;
    }

    public UniversalResponse sendMoney(SendMoneyDTO request){

        if (helperUtility.checkPin(request.getPin(), request.getSenderAccountNumber())){
            try{

                if (helperUtility.checkAccount(request.getReceiverAccountNumber())){
                    return UniversalResponse.builder()
                            .message("The Mission customer does not exist")
                            .status("1")
                            .build();
                }
                else {
                    
                    try {
                        Account fromaccount=accountRepository.findByAccountNumber(request.getSenderAccountNumber());
                        double Balance= fromaccount.getAccountBalance();
                        double SendingAmount=request.getTransactionAmount();

                        if(Balance> SendingAmount){
                            double FromBalance =Balance- SendingAmount;
                            double ToBalance=Balance+ SendingAmount;
                            
                            try {
                                Account Toaccount = accountRepository.findByAccountNumber(request.getReceiverAccountNumber());
                                Toaccount.setAccountBalance(ToBalance);
                                accountRepository.save(Toaccount);
                                Account FromAccount = accountRepository.findByAccountNumber(request.getSenderAccountNumber());
                                FromAccount.setAccountBalance(FromBalance);
                                accountRepository.save(FromAccount);

                                //TODO: Implement code to Insert into transaction Table here
//
                                try {

                                    Transaction trans = Transaction.builder()
                                            .transactionAmount(request.getTransactionAmount())
                                            .transactionType("SENDMONEY")
                                            .ReferenceCode(referenceCode)
                                            .senderAccount(request.getSenderAccountNumber())
                                            .receiverAccount(request.getReceiverAccountNumber())
                                            .Debited(request.getTransactionAmount())
                                            .Credited(request.getTransactionAmount())
                                            .status("success")
                                            .build();
                                    transactionRepository.save(trans);

                                    sendMoneyResponseDTO.setTransactionAmount(request.getTransactionAmount());
                                    sendMoneyResponseDTO.setReceiverAccountNumber(request.getReceiverAccountNumber());
                                    return UniversalResponse.builder()
                                            .message("Transaction successful")
                                            .status("1")
                                            .data(sendMoneyResponseDTO)
                                            .build();
                                }
                                catch (Exception ex){
                                    logs.log(ex.getMessage());

                                    Transaction trans = Transaction.builder()
                                            .transactionAmount(request.getTransactionAmount())
                                            .transactionType("SENDMONEY")
                                            .ReferenceCode(referenceCode)
                                            .senderAccount(request.getSenderAccountNumber())
                                            .receiverAccount(request.getReceiverAccountNumber())
                                            .Debited(request.getTransactionAmount())
                                            .Credited(request.getTransactionAmount())
                                            .status("failed")
                                            .build();
                                    transactionRepository.save(trans);
                                }

                                try {
                                    Message twilioMessage = Message.creator(
                                                    new PhoneNumber(request.getReceiverAccountNumber()),
                                                    new PhoneNumber(twilioConfig.getTrial_number()),
                                                    "You have received Ksh:"
                                                            + request.getTransactionAmount() + "From"
                                                            + request.getSenderAccountNumber()
                                                            + "You new Account Balance is Ksh:"
                                                            + ToBalance)
                                                    .create();

                                    twilioMessage = Message.creator(
                                                    new PhoneNumber(request.getSenderAccountNumber()),
                                                    new PhoneNumber(twilioConfig.getTrial_number()),
                                                    "You have Sent Ksh:" + request.
                                                            getTransactionAmount() + "To"
                                                            + request.getReceiverAccountNumber()
                                                            + "You new Account Balance is Ksh:" + FromBalance)
                                                    .create();
                                }
                                catch (Exception ex) {
                                    System.out.println(ex);
                                    return UniversalResponse.builder()
                                            .message("Error while sending transaction message")
                                            .status("1")
                                            .build();
                                }
                            }
                            catch(Exception ex){
                                System.out.println(ex);
                                return  UniversalResponse.builder()
                                        .message("Transaction Error")
                                        .status("1")
                                        .build();
                            }
                        }
                        else{

                            Transaction trans = Transaction.builder()
                                    .transactionAmount(request.getTransactionAmount())
                                    .transactionType("SENDMONEY")
                                    .ReferenceCode(referenceCode)
                                    .senderAccount(request.getSenderAccountNumber())
                                    .receiverAccount(request.getReceiverAccountNumber())
                                    .Debited(request.getTransactionAmount())
                                    .Credited(request.getTransactionAmount())
                                    .status("failed")
                                    .build();
                            transactionRepository.save(trans);

                            return  UniversalResponse.builder()
                                    .message("Transaction failed insufficient funds")
                                    .status("1")
                                    .data("Account balance"+fromaccount.getAccountBalance())
                                    .build();
                        }
                    }
                    catch (Exception ex){
                        logs.log(ex.getMessage());
                    }
                }
            }
            catch (Exception ex){
                System.out.println(ex);
                return  UniversalResponse.builder()
                        .message("Transaction Failed")
                        .status("1")
                        .build();
            }
        }
        else {
            return  UniversalResponse.builder()
                    .message("Wrong pin!")
                    .status("1")
                    .build();
        }

        return response;
    }
}
