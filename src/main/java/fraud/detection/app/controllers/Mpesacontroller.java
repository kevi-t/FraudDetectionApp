package fraud.detection.app.controllers;

import fraud.detection.app.dto.AccessTokenResponse;
import fraud.detection.app.dto.InternalStkPushRequest;
import fraud.detection.app.dto.StkPushSyncResponse;
import fraud.detection.app.services.DarajaApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @PostMapping(path = "/stk-transaction-request", produces = "application/json")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {
        return ResponseEntity.ok(darajaApi.performStkPushTransaction(internalStkPushRequest));
    }
}
