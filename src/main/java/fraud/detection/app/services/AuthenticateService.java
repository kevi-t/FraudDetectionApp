<<<<<<< HEAD
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
             Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPin()));
             SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex){
            return UniversalResponse.builder().message("Username or Pin incorrect").build();
        }
        try {
            User user = userRepository.findUserByMobileNumber(request.getMobileNumber());
            if (user == null) {
                return UniversalResponse.builder().message("User not found, Please Register").build();
            }
            else {
                try{
                    String jwt = jwtTokenUtil.createToken(request.getMobileNumber());
                    Account accountNumber = accountRepository.findByAccountNumber(request.getMobileNumber());
                    return  UniversalResponse.builder().message("Login Successful").data(jwt).build();
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
=======
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
             Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPin()));
             SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex){
            return UniversalResponse.builder().message("Username or Pin incorrect").build();
        }
        try {
            User user = userRepository.findUserByMobileNumber(request.getMobileNumber());
            if (user == null) {
                return UniversalResponse.builder().message("User not found, Please Register").build();
            }
            else {
                try{
                    String jwt = jwtTokenUtil.createToken(request.getMobileNumber());
                    Account accountNumber = accountRepository.findByAccountNumber(request.getMobileNumber());
                    return  UniversalResponse.builder().message("Login Successful").data(jwt).build();
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
>>>>>>> 625a600188ea8c8ab74c6ee5d9ceeab8e4143e05
}