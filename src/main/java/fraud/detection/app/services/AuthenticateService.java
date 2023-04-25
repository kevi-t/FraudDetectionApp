package fraud.detection.app.services;


import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.dto.LoginResponse;
import fraud.detection.app.models.User;
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
    private final UniversalResponse universalResponseresponse;
    private final LoginResponse loginResponse;

    public AuthenticateService(JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, UserRepository userRepository, UniversalResponse universalResponseresponse, LoginResponse loginResponse) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.universalResponseresponse = universalResponseresponse;
        this.loginResponse = loginResponse;
    }

    public UniversalResponse login(AuthenticationDTO request) {
        String checkedNumber = checkPhoneNumber(request.getMobileNumber());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(checkedNumber, request.getPin()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex){
            log.info("Username or pin incorrect");
            return  UniversalResponse.builder()
                    .message("Username or pin incorrect")
                    .status("1")
                    .build();
        }
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
                try{
                    String jwt = jwtTokenUtil.createToken(checkedNumber);
                    loginResponse.setToken(jwt);
                    loginResponse.setUserPhoneNumber(request.getMobileNumber());
                    loginResponse.setUserEmail(user.getEmail().toString());
                    return UniversalResponse.builder()
                            .message("login sucessful")
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
        }
        catch (Exception ex){
            log.info("Error while finding user"+ex);
            ex.printStackTrace();
        }
        return universalResponseresponse;
    }

}