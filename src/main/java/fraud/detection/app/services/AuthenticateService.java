package fraud.detection.app.services;


import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.UserRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    public UniversalResponse response;

    public UniversalResponse login(AuthenticationDTO request) {
        try {
             Authentication authentication = authenticationManager
                     .authenticate(new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPin()));
             SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex){
            UniversalResponse response= UniversalResponse.builder()
                    .message("Username or pin Incorrect")
                    .status("failed")
                    .data(request)
                    .build();
            return  response;
        }
        try {
            User user = userRepository.findUserBymobileNumber(request.getMobileNumber());
            if (user == null) {
                UniversalResponse response= UniversalResponse.builder()
                        .message("user not found please register")
                        .status("failed")
                        .data(request)
                        .build();
                return  response;
            }
            else {
                try{
                    String jwt = jwtTokenUtil.createToken(request.getMobileNumber());
                    Account accountNumber = accountRepository.findByAccountNumber(request.getMobileNumber());
                    UniversalResponse response= UniversalResponse.builder()
                            .message("Login successful")
                            .status("success")
                            .data(jwt)
                            .build();
                    return  response;
                }
                catch (Exception ex){
                    System.out.println("Token failure");
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return response;
    }

}