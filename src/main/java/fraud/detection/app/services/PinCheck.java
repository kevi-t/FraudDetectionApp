package fraud.detection.app.services;

import fraud.detection.app.dto.CheckPin;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PinCheck {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Boolean checkPin(String pin,String senderaccount){
        User user=userRepository.findUserBymobileNumber(senderaccount);
        String dbPin= user.getPin();
        if (passwordEncoder.matches(pin, dbPin));

        return true;
    }
}
