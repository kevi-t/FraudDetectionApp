package fraud.detection.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fraud.detection.app.configurations.MpesaConfiguration;
import fraud.detection.app.dto.mpesa.AccessTokenResponse;
import fraud.detection.app.dto.mpesa.AcknowledgeResponse;
import fraud.detection.app.dto.mpesa.InternalStkPushRequest;
import fraud.detection.app.dto.mpesa.StkPushSyncResponse;
import fraud.detection.app.models.StkPush_Entries;
import fraud.detection.app.repositories.StkPushEntriesRepository;
import fraud.detection.app.services.DarajaApi;
import fraud.detection.app.services.DarajaApiImpl;
import fraud.detection.app.utils.HelperUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static fraud.detection.app.utils.Constants.*;

@RestController
@Slf4j
@RequestMapping("mobile-money")
public class Mpesacontroller {
private final StkPushEntriesRepository stkPushEntriesRepository;
private final AcknowledgeResponse acknowledgeResponse;
private final DarajaApiImpl darajaApiImpl;
private final MpesaConfiguration mpesaConfiguration;
      private final DarajaApi darajaApi;
    private final ObjectMapper objectMapper;
    private  final OkHttpClient okHttpClient;

    public Mpesacontroller(StkPushEntriesRepository stkPushEntriesRepository
            , AcknowledgeResponse acknowledgeResponse
            , DarajaApiImpl darajaApiImpl, MpesaConfiguration mpesaConfiguration, DarajaApi darajaApi, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        this.stkPushEntriesRepository = stkPushEntriesRepository;
        this.acknowledgeResponse = acknowledgeResponse;
        this.darajaApiImpl = darajaApiImpl;
        this.mpesaConfiguration = mpesaConfiguration;
        this.darajaApi = darajaApi;
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
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
        AccessTokenResponse Token =darajaApiImpl.getAccessToken();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(stkPushSyncResponse)));
        Request request = new Request.Builder()
                .url(mpesaConfiguration.getStkPushRequestUrl())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, Token.getAccessToken()))
                .build();

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
        Response response = okHttpClient.newCall(request).execute();
        stkPushEntriesRepository.save(StkpushEntry);
        return ResponseEntity.ok(acknowledgeResponse);
    }
}
