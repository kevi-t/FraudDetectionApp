package fraud.detection.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fraud.detection.app.dto.*;
import fraud.detection.app.models.StkPush_Entries;
import fraud.detection.app.repositories.StkPushEntriesRepository;
import fraud.detection.app.services.DarajaApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("mobile-money")
public class Mpesacontroller {
private final StkPushEntriesRepository stkPushEntriesRepository;
private final AcknowledgeResponse acknowledgeResponse;
      private final DarajaApi darajaApi;
    private final ObjectMapper objectMapper;

    public Mpesacontroller(StkPushEntriesRepository stkPushEntriesRepository
            , AcknowledgeResponse acknowledgeResponse
            ,DarajaApi darajaApi, ObjectMapper objectMapper) {
        this.stkPushEntriesRepository = stkPushEntriesRepository;
        this.acknowledgeResponse = acknowledgeResponse;
        this.darajaApi = darajaApi;
        this.objectMapper = objectMapper;
    }


    @GetMapping(path = "/token", produces = "application/json")
        public ResponseEntity<AccessTokenResponse> getAccessToken() {
            return ResponseEntity.ok(darajaApi.getAccessToken());
        }
    @PostMapping(path = "/stk-deposit-request", produces = "application/json")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {

        return ResponseEntity.ok(darajaApi.DepositStkPushTransaction(internalStkPushRequest));
    }
    @SneakyThrows
    @PostMapping(path = "/stk-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushSyncResponse stkPushSyncResponse) {
        log.info("======= STK Push Async Response =====");
        log.info(objectMapper.writeValueAsString(stkPushSyncResponse));
        StkPush_Entries stkObj= new StkPush_Entries();
        var StkpushEntry=stkObj.builder()
                .checkoutRequestID(stkPushSyncResponse.getCheckoutRequestID())
                .merchantRequestID(stkPushSyncResponse.getMerchantRequestID())
                .responseCode(stkPushSyncResponse.getResponseCode())
                .responseDescription(stkPushSyncResponse.getResponseDescription())
                .customerMessage(stkPushSyncResponse.getCustomerMessage())
                .build();
        stkPushEntriesRepository.save(StkpushEntry);
        return ResponseEntity.ok(acknowledgeResponse);
    }
}
