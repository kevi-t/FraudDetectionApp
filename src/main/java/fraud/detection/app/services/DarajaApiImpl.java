package fraud.detection.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import fraud.detection.app.configurations.MpesaConfiguration;
import fraud.detection.app.dto.mpesa.*;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.responses.UniversalResponse;
import fraud.detection.app.utils.Constants;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static fraud.detection.app.utils.Constants.*;
import static fraud.detection.app.utils.HelperUtility.checkPhoneNumber;

@Service
@Slf4j
public class DarajaApiImpl implements DarajaApi {

    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final TransactionRepository transactionRepository;
    private final HelperUtility helperUtility;
    private final AccountRepository accountRepository;
    private StkPushSyncResponse stkPushSyncResponse;
    private double updatedAccountBalance;

    public DarajaApiImpl(MpesaConfiguration mpesaConfiguration,
                         OkHttpClient okHttpClient,
                         ObjectMapper objectMapper,
                         TransactionRepository transactionRepository,
                         HelperUtility helperUtility,
                         StkPushSyncResponse stkPushSyncResponse,
                         AccountRepository accountRepository) {
        this.mpesaConfiguration = mpesaConfiguration;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
        this.helperUtility = helperUtility;
        this.stkPushSyncResponse = stkPushSyncResponse;
        this.accountRepository = accountRepository;
    }

    /**
     * Request and return the access token from Safaricom
     */
    @Override
    public AccessTokenResponse getAccessToken() {
        String encodedCredentials = HelperUtility.toBase64String(
                String.format("%s:%s", mpesaConfiguration.getConsumerKey(), mpesaConfiguration.getConsumerSecret())
        );

        String url = String.format("%s?grant_type=%s",
                mpesaConfiguration.getOauthEndpoint(),
                mpesaConfiguration.getGrantType());

        // Log what we are sending
        log.info("Daraja AccessToken Request URL: {}", url);
        log.info("Daraja Authorization Header: Basic {}", encodedCredentials);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();
        log.info("Request URL: {}", request.url());
        log.info("Request Headers: {}", request.headers().toMultimap());
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "null";
                log.error("Failed to fetch access token. HTTP {} - {}", response.code(), errorBody);
                return null;
            }
            String body = response.body() != null ? response.body().string() : "";
            log.info("Access Token Response: {}", body);
            return objectMapper.readValue(body, AccessTokenResponse.class);
        } catch (IOException e) {
            log.error("Could not get access token. Exception -> {}", e.getMessage(), e);
            return null;
        }
    }
    @Override
    public AccessTokenResponse registerUrl() {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        log.info("HEERE"+accessTokenResponse);
        if (accessTokenResponse == null) {
            log.error("Access token is null, cannot register URL.");
            return null;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"BusinessShortCode\": 174379," +
                        "\"Password\": \"...YourGeneratedPassword...\"," +
                        "\"Timestamp\": \"20230411111428\"," +
                        "\"TransactionType\": \"CustomerPayBillOnline\"," +
                        "\"Amount\": 1," +
                        "\"PartyA\": 254711694281," +
                        "\"PartyB\": 174379," +
                        "\"PhoneNumber\": 254711694281," +
                        "\"CallBackURL\": \"https://mydomain.com/path\"," +
                        "\"AccountReference\": \"CompanyXLTD\"," +
                        "\"TransactionDesc\": \"Payment of X\"}"
        );

        Request request = new Request.Builder()
                .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "null";
                log.error("Failed to register URL. HTTP {} - {}", response.code(), errorBody);
                return null;
            }
            String resp = response.body() != null ? response.body().string() : "";
            log.info("Register URL Response: {}", resp);
            return objectMapper.readValue(resp, AccessTokenResponse.class);
        } catch (IOException e) {
            log.error("Could not register URL. Exception -> {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public UniversalResponse DepositStkPushTransaction(InternalStkPushRequest internalStkPushRequest) {
        String externalPin = internalStkPushRequest.getPin();
        String checkedAccountNumber = checkPhoneNumber(internalStkPushRequest.getAccountNo());

        if (!helperUtility.checkPin(externalPin, checkedAccountNumber)) {
            try {
                Message.creator(
                        new PhoneNumber("+254112016790"),
                        new PhoneNumber(checkedAccountNumber),
                        "You entered the wrong pin"
                ).create();
            } catch (Exception e) {
                log.error("Could not send SMS notification -> {}", e.getMessage());
            }
            return UniversalResponse.builder().message("Wrong Pin").status("1").build();
        }

        ExternalStkPushRequest externalStkPushRequest = new ExternalStkPushRequest();
        String transactionTimestamp = HelperUtility.getTransactionTimestamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(
                mpesaConfiguration.getStkPushShortCode(),
                mpesaConfiguration.getStkPassKey(),
                transactionTimestamp
        );

        externalStkPushRequest.setBusinessShortCode(mpesaConfiguration.getStkPushShortCode());
        externalStkPushRequest.setPassword(stkPushPassword);
        externalStkPushRequest.setTimestamp(transactionTimestamp);
        externalStkPushRequest.setTransactionType(Constants.CUSTOMER_PAYBILL_ONLINE);
        externalStkPushRequest.setAmount(internalStkPushRequest.getAmount());
        externalStkPushRequest.setPartyA(checkedAccountNumber);
        externalStkPushRequest.setPartyB(mpesaConfiguration.getStkPushShortCode());
        externalStkPushRequest.setPhoneNumber(checkedAccountNumber);
        externalStkPushRequest.setCallBackURL(mpesaConfiguration.getStkPushRequestCallbackUrl());
        externalStkPushRequest.setAccountReference(HelperUtility.getTransactionUniqueNumber());
        externalStkPushRequest.setTransactionDesc(
                String.format("%s Transaction", internalStkPushRequest.getPhoneNumber())
        );

        AccessTokenResponse accessTokenResponse = getAccessToken();
        if (accessTokenResponse == null) {
            log.error("Access token is null, cannot perform STK push.");
            return UniversalResponse.builder().message("Access Token Error").status("0").build();
        }

        RequestBody body = RequestBody.create(
                JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(externalStkPushRequest))
        );

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getStkPushRequestUrl())
                .post(body)
                .addHeader("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "null";
                log.error("STK Push request failed. HTTP {} - {}", response.code(), errorBody);
                return UniversalResponse.builder().message("STK Push Failed").status("0").build();
            }

            String resp = response.body() != null ? response.body().string() : "";
            log.info("STK Push Response: {}", resp);

            Transaction trans = Transaction.builder()
                    .transactionAmount(internalStkPushRequest.getAmount())
                    .transactionType("DEPOSIT")
                    .ReferenceCode(HelperUtility.getTransactionUniqueNumber())
                    .receiverAccount(internalStkPushRequest.getPhoneNumber())
                    .senderAccount(internalStkPushRequest.getPhoneNumber())
                    .status("success")
                    .build();
            transactionRepository.save(trans);

            Account transactionAccount = accountRepository.findByAccountNumber(checkedAccountNumber);
            double currentBalance = transactionAccount.getAccountBalance();
            updatedAccountBalance = currentBalance + internalStkPushRequest.getAmount();
            transactionAccount.setAccountBalance(updatedAccountBalance);
            transactionAccount.setBalanceBefore(currentBalance);
            accountRepository.save(transactionAccount);

            stkPushSyncResponse = objectMapper.readValue(resp, StkPushSyncResponse.class);

            return UniversalResponse.builder()
                    .data(stkPushSyncResponse)
                    .status("1")
                    .message("Request accepted for processing")
                    .build();

        } catch (IOException e) {
            log.error("Could not perform the STK push request -> {}", e.getMessage(), e);
            return UniversalResponse.builder().message("STK Push Exception").status("0").build();
        }
    }

    @Override
    public B2CTransactionSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest) {
        AccessTokenResponse accessTokenResponse = getAccessToken();
        if (accessTokenResponse == null) {
            log.error("Access token is null, cannot perform B2C transaction.");
            return null;
        }

        B2CTransactionRequest b2CTransactionRequest = new B2CTransactionRequest();
        b2CTransactionRequest.setCommandID(mpesaConfiguration.getCommandID());
        b2CTransactionRequest.setAmount(internalB2CTransactionRequest.getAmount());
        b2CTransactionRequest.setPartyB(internalB2CTransactionRequest.getPartyB());
        b2CTransactionRequest.setRemarks(internalB2CTransactionRequest.getRemarks());
        b2CTransactionRequest.setOccassion(internalB2CTransactionRequest.getOccassion());
        b2CTransactionRequest.setSecurityCredential(
                HelperUtility.getSecurityCredentials(mpesaConfiguration.getB2cInitiatorPassword())
        );
        b2CTransactionRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        b2CTransactionRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        b2CTransactionRequest.setInitiatorName(mpesaConfiguration.getB2cInitiatorName());
        b2CTransactionRequest.setPartyA(mpesaConfiguration.getShortCode());

        RequestBody body = RequestBody.create(
                JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(b2CTransactionRequest))
        );

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .post(body)
                .addHeader("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "null";
                log.error("B2C Transaction failed. HTTP {} - {}", response.code(), errorBody);
                return null;
            }

            String resp = response.body() != null ? response.body().string() : "";
            log.info("B2C Response: {}", resp);

            return objectMapper.readValue(resp, B2CTransactionSyncResponse.class);

        } catch (IOException e) {
            log.error("Could not perform B2C transaction -> {}", e.getMessage(), e);
            return null;
        }
    }
}