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
                    try {
                        Message.creator(new PhoneNumber(request.getAccountNumber()),
                                new PhoneNumber(twilioConfiguration.getTrial_number()),
                                referenceCode + "Confirmed Account balance. Ksh" + account.getAccountBalance()).create();
                    }
                    catch (Exception ex) {
                        System.out.println("Error While Sending Transaction Message" + ex);
                        return UniversalResponse.builder().message("Error While Sending Transaction Message").status("failed").build();
                    }
                    return UniversalResponse.builder().message("Balance request successful account balance:" + account.getAccountBalance()).build();

                }
                catch (Exception ex){
                    return UniversalResponse.builder().message("Transaction Error").status("failed").build();
                }
            }
            else {
                return UniversalResponse.builder().message("Wrong pin!").status("failed").build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}