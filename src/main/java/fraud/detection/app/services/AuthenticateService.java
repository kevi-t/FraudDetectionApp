package fraud.detection.app.services;


import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.dto.LoginResponse;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@Slf4j
@Service
public class AuthenticateService {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UniversalResponse response;
    private final LoginResponse loginResponse;
    private final AccountRepository accountRepository;

    public AuthenticateService(JwtTokenUtil jwtTokenUtil,
                               AuthenticationManager authenticationManager,
                               UserRepository userRepository,
                               UniversalResponse response,
                               LoginResponse loginResponse,
                               AccountRepository accountRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.response = response;
        this.loginResponse = loginResponse;
        this.accountRepository = accountRepository;
    }

    public UniversalResponse login(AuthenticationDTO request) {

        String checkedNumber = checkPhoneNumber(request.getMobileNumber());
        try {
            User user = userRepository.findUserByMobileNumber(checkedNumber);
            if (user == null) {
                log.info("User not found please register");
                return  UniversalResponse.builder()
                        .message("User not found please register")
                        .status("1")
                        .build();
            }
            else {

                try {
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(checkedNumber, request.getPin()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    try{
                        Account account=accountRepository.findByAccountNumber(checkedNumber);
                        String jwt = jwtTokenUtil.createToken(checkedNumber);
                        loginResponse.setToken(jwt);
                        loginResponse.setUserPhoneNumber(request.getMobileNumber());
                        loginResponse.setUserName(user.getFirstName().toString());
                        loginResponse.setBalance(account.getAccountBalance());
                        log.info("login successful");
                        return UniversalResponse.builder()
                                .message("login successful")
                                .status("1")
                                .data(loginResponse)
                                .build();
                    }
                    catch (Exception ex){
                        log.info("Failed to generate token"+ex);
                        return UniversalResponse.builder()
                                .message("Failed to generate token"+ex)
                                .status("1")
                                .build();
                    }
                }
                catch (Exception ex){
                    log.info("Username or pin incorrect");
                    return  UniversalResponse.builder()
                            .message("Username or pin incorrect")
                            .status("1")
                            .build();
                }
            }
        }
        catch (Exception ex){
            log.info("Error while finding user"+ex);
            ex.printStackTrace();
        }
        return response;
    }

}