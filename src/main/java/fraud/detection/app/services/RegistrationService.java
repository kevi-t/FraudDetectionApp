package fraud.detection.app.services;

import fraud.detection.app.dto.RegisterDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    public UniversalResponse response;

    public UniversalResponse register(RegisterDTO  request) {

        String phone=request.getMobileNumber();
        try{
            if (userRepository.findUserByEmail(request.getEmail())!=null){
                return  UniversalResponse.builder().message("User with this email Already Exist").status(0).build();
            }
            else if(userRepository.findUserByMobileNumber(phone)==null) {

                var user = User.builder()
                        .firstName(request.getFirstName())
                        .middleName(request.getMiddleName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .mobileNumber(request.getMobileNumber())
                        .pin(passwordEncoder.encode(request.getPin()))
                        .gender(request.getGender())
                        .dateOfBirth(request.getDateOfBirth())
                        .occupation(request.getOccupation())
                        .country(request.getCountry())
                        .city(request.getCity())
                        .state(request.getState())
                        .currentAddress(request.getCurrentAddress())
                        .permanentAddress(request.getPermanentAddress())
                        .pinCode(request.getPinCode()).build();
                try {
                    userRepository.save(user);
                    var account = Account.builder()
                            .accountNumber(user.getMobileNumber())
                            .openedBy(user.getMobileNumber())
                            .accountBalance(0.55)
                            .balanceBefore(0.01)
                            .build();//.user(user)
                    accountRepository.save(account);
                    return  UniversalResponse.builder().message("User Registered Successfully").status(0).build();
                }
                catch (Exception ex){
                    System.out.println("Registration failed{}" + ex);
                }
            }
            else {
                return   UniversalResponse.builder().message("User Already Registered, Please Login").status(0).build();
            }
        }
        catch (Exception ex){
            System.out.println("Saving User Failed{}"+ex);
            //TODO: Save method to be a boolean,if fails to save send error
        }
        return  response;
    }
}