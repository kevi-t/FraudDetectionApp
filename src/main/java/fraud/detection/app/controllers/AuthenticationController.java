package fraud.detection.app.controllers;
import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.AuthenticateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fraud/app")
public class AuthenticationController {

   private final AuthenticateService authenticateService;

   @PostMapping("/login")
   public ResponseEntity<UniversalResponse> login(@RequestBody AuthenticationDTO request) {
      return ResponseEntity.ok(authenticateService.login(request));
   }

}