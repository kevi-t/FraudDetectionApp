package fraud.detection.app.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fraud.detection.app.configurations.TwilioConfiguration;
import fraud.detection.app.dto.CheckBalanceDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CheckBalanceService {

    private final AccountRepository accountRepository;
    private  final TwilioConfiguration twilioConfiguration;
    public UniversalResponse response;
    public final HelperUtility helperUtility;
    String referenceCode = HelperUtility.referenceCodeGenerator();

    @Autowired
    public CheckBalanceService(AccountRepository accountRepository,
                               TwilioConfiguration twilioConfiguration,
                               HelperUtility helperUtility) {
        this.accountRepository = accountRepository;
        this.twilioConfiguration = twilioConfiguration;
        this.helperUtility = helperUtility;
    }

    public UniversalResponse checkBalance(CheckBalanceDTO request) {
        try {
            if (helperUtility.checkPin(request.getPin(), request.getAccountNumber())){

                try{

                    Account account=accountRepository.findByAccountNumber(request.getAccountNumber());
                    BigDecimal accountBalance = BigDecimal.valueOf(account.getAccountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP);
//                    try {
//                        Message.creator(new PhoneNumber(request.getAccountNumber()),
//                                new PhoneNumber(twilioConfiguration.getTrial_number()),
//                                referenceCode + "Confirmed Account balance. Ksh"
//                                        + accountBalance.create();
//                    }
//                    catch (Exception ex) {
//                        System.out.println("Error while sending transaction message" + ex);
//                        return UniversalResponse.builder()
//                                .message("Error while sending transaction message")
//                                .status("1")
//                                .build();
//                    }



                    return UniversalResponse.builder()
                            .message("request successful")
                            .status("0")
                            .data(accountBalance)
                            .build();

                }
                catch (Exception ex){
                    return UniversalResponse.builder()
                            .message("Transaction Error")
                            .status("1")
                            .build();
                }
            }
            else {
                return UniversalResponse.builder()
                        .message("Wrong pin!")
                        .status("1")
                        .build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}