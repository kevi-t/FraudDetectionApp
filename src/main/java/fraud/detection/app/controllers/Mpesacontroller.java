package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccessTokenResponse;
import fraud.detection.app.dto.RegisterUrlResponse;
import fraud.detection.app.services.DarajaApi;
import fraud.detection.app.services.DarajaApiImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mobile-money")
public class Mpesacontroller {

      private final DarajaApi darajaApi;

    public Mpesacontroller(DarajaApi darajaApi) {
        this.darajaApi = darajaApi;
    }


    @GetMapping(path = "/token", produces = "application/json")
        public ResponseEntity<AccessTokenResponse> getAccessToken() {
            return ResponseEntity.ok(darajaApi.getAccessToken());
        }
    @PostMapping(path = "/register-url", produces = "application/json")
    public ResponseEntity<RegisterUrlResponse> registerUrl() {
        return ResponseEntity.ok(darajaApi.registerUrl());
    }
}
