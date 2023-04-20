package fraud.detection.app.services;


import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.models.User;
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
    public UniversalResponse response;

    public UniversalResponse login(AuthenticationDTO request) {
        try {
             Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getMobileNumber(), request.getPin()));
             SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception ex){
            return  UniversalResponse.builder()
                    .message("Username or pin incorrect")
                    .status("1")
                    .data(request)
                    .build();
        }
        try {
            User user = userRepository.findUserByMobileNumber(request.getMobileNumber());
            if (user == null) {
                return  UniversalResponse.builder()
                        .message("User not found please register")
                        .status("1")
                        .data(request)
                        .build();
            }
            else {
                try{
                    String jwt = jwtTokenUtil.createToken(request.getMobileNumber());
                    return UniversalResponse.builder()
                            .message("Login successful")
                            .status("0")
                            .data(jwt)
                            .build();
                }
                catch (Exception ex){
                    return UniversalResponse.builder()
                            .message("Failed to generate token")
                            .status("1")
                            .build();
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return response;
    }

}