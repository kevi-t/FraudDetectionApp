package fraud.detection.app.controllers;
import fraud.detection.app.dto.AuthenticationDTO;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.services.AuthenticateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fraud/app")
@Validated
@Slf4j
public class AuthenticationController {

   private final AuthenticateService authenticateService;

   @PostMapping("/login")
   public ResponseEntity<UniversalResponse> login(@Valid @RequestBody AuthenticationDTO request) {
      try{
         return ResponseEntity.ok(authenticateService.login(request));
      }
      catch (Exception ex){
         return new ResponseEntity(HttpStatus.BAD_REQUEST);
      }

   }

}