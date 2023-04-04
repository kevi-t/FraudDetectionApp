package fraud.detection.app.services;

import fraud.detection.app.dto.RegisterDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
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
   // private  final AccountRepository accountRepository;
private final PasswordEncoder passwordEncoder;
    public UniversalResponse response;
    public UniversalResponse register(RegisterDTO request) {
        String phone=request.getMobileNumber();
        String Email= request.getEmail();
        //TODO check if the email also exists
try{
    if (userRepository.findUserByMobileNumber(phone)==null) {
                            User userObj = new User();
                    var user = userObj.builder()
                            .city(request.getCity()).
                            country(request.getCountry())
                            .currentAddress(request.getCurrentAddress())
                            .dateOfBirth(request.getDateOfBirth())
                            .email(request.getEmail())
                            .firstName(request.getFirstName())
                            .gender(request.getGender())
                            .lastName(request.getLastName())
                            .mobileNumber(request.getMobileNumber())
                            .state(request.getState()).
                            middleName(request.getMiddleName())
                            .occupation(request.getOccupation())
                            .permanentAddress(request.getPermanentAddress())
                            .pinCode(request.getPinCode())
                            .pin(passwordEncoder.encode(request.getPin()))
                            .build();
                    Account AccObj=new Account();
                    var account=AccObj.builder().balance(2000.000)
                            .balanceBefore(0.000)
                            .openedBy(request.getMobileNumber())
                            .user(user)
                            .build();
                    user.setAccount(account);
                    try {
                        //accountRepository.save(account);
                        userRepository.save(user);
                        UniversalResponse response= UniversalResponse.builder()
                                .message("User saved Successfully")
                                .status(0)
                                .build();
                        return  response;
                    }catch (Exception ex){

                        System.out.println("Saving user failed{}" + ex);
                    }

                }
               else {

                UniversalResponse response= UniversalResponse.builder()
                        .message("User Already Registered, Please Login")
                        .status(0)
                        .build();

                return  response;
            }

}
        catch (Exception ex){
           System.out.println("Saving User Failedd{}"+ex);
//TODO: Save method to be a bolean,if fails to save send error
        }
        return  response;}
    }
