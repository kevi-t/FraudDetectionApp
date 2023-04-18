package fraud.detection.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import fraud.detection.app.configurations.MpesaConfiguration;
import fraud.detection.app.dto.mpesa.*;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.Transaction;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.TransactionRepository;
import fraud.detection.app.utils.Constants;
import fraud.detection.app.utils.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static fraud.detection.app.utils.Constants.*;

@Service
@Slf4j
public class DarajaApiImpl  implements DarajaApi{
    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final TransactionRepository transactionRepository;
    private final HelperUtility helperUtility;
    public StkPushSyncResponse stkPushSyncResponse;
    private final AccountRepository accountRepository;
    double UpdatedAccountBalance;

    public DarajaApiImpl(MpesaConfiguration mpesaConfiguration, OkHttpClient okHttpClient
            , ObjectMapper objectMapper
            , TransactionRepository transactionRepository, HelperUtility helperUtility
            , StkPushSyncResponse stkPushSyncResponse
            , AccountRepository accountRepository) {
        this.mpesaConfiguration = mpesaConfiguration;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
        this.helperUtility = helperUtility;
        this.stkPushSyncResponse = stkPushSyncResponse;
        this.accountRepository = accountRepository;
    }


    /**
     * @return Returns Daraja API Access Token Response
     */
    @Override
    public AccessTokenResponse getAccessToken() {

        // get the Base64 rep of consumerKey + ":" + consumerSecret
        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(),
                mpesaConfiguration.getConsumerSecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    public AccessTokenResponse registerUrl() throws IOException {
        AccessTokenResponse accessTokenResponse = getAccessToken();

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest();
        registerUrlRequest.setConfirmationURL(mpesaConfiguration.getConfirmationUrl());
        registerUrlRequest.setResponseType(mpesaConfiguration.getResponseType());
        registerUrlRequest.setShortCode(mpesaConfiguration.getShortCode());
        registerUrlRequest.setValidationURL(mpesaConfiguration.getValidationUrl());

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), "{\"BusinessShortCode\": 174379, " +
                "\"Password\": \"MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjMwNDExMTExNDI4\", " +
                "\"Timestamp\": \"20230411111428\", " +
                "\"TransactionType\": " +
                "\"CustomerPayBillOnline\", " +
                "\"Amount\": 1, "                                                                                                                                                                                                                                                                            +
                "\"PartyA\": 254711694281, " +
                "\"PartyB\": 174379, " +
                "\"PhoneNumber\": 254711694281, " +
                "\"CallBackURL\": \"https://mydomain.com/path\", " +
                "\"AccountReference\": " +
                "\"CompanyXLTD\", " +
                "\"TransactionDesc\": " +
                "\"Payment of X\"}");
        Request request = new Request.Builder()

                .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")

                .method("POST", body)

                .addHeader("Content-Type", "application/json")

                .addHeader("Authorization", "Bearer"+accessTokenResponse.getAccessToken())

                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            // use Jackson to Decode the ResponseBody ...
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    @Override
    public StkPushSyncResponse DepositStkPushTransaction(InternalStkPushRequest internalStkPushRequest) {
        String ExtenalPin = internalStkPushRequest.getPin();
        String Accountno = internalStkPushRequest.getPhoneNumber();
        if (helperUtility.checkPin(ExtenalPin, Accountno)==false) {

//    ToDo: Send A message to the user telling them they entered the wrong pin
            try {
                Message twilioMessage = Message.creator(
                                new PhoneNumber("+254 112 016790"),
                                new PhoneNumber(internalStkPushRequest.getPhoneNumber()),
                                "You entered the wrong Pin")
                        .create();
            }
            catch (Exception e) {

                log.error(String.format("Could not perform sending messages request -> %s"
                        , e.getLocalizedMessage()));

            }}
        else{
                ExternalStkPushRequest externalStkPushRequest = new ExternalStkPushRequest();
                //need from mobile session
                externalStkPushRequest.setBusinessShortCode(mpesaConfiguration.getStkPushShortCode());

                String transactionTimestamp = HelperUtility.getTransactionTimestamp();
                String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfiguration.getStkPushShortCode(),
                        mpesaConfiguration.getStkPassKey(), transactionTimestamp);
                externalStkPushRequest.setPassword(stkPushPassword);
                externalStkPushRequest.setTimestamp(transactionTimestamp);
                externalStkPushRequest.setTransactionType(Constants.CUSTOMER_PAYBILL_ONLINE);
                externalStkPushRequest.setAmount(internalStkPushRequest.getAmount());
                externalStkPushRequest.setPartyA(internalStkPushRequest.getPhoneNumber());
                externalStkPushRequest.setPartyB(mpesaConfiguration.getStkPushShortCode());
                externalStkPushRequest.setPhoneNumber(internalStkPushRequest.getPhoneNumber());
                externalStkPushRequest.setCallBackURL(mpesaConfiguration.getStkPushRequestCallbackUrl());
                externalStkPushRequest.setAccountReference(HelperUtility.getTransactionUniqueNumber());
                externalStkPushRequest.setTransactionDesc(String.format("%s Transaction", internalStkPushRequest.getPhoneNumber()));

            AccessTokenResponse accessTokenResponse = getAccessToken();
                RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                        Objects.requireNonNull(HelperUtility.toJson(externalStkPushRequest)));
                Request request = new Request.Builder()
                        .url(mpesaConfiguration.getStkPushRequestUrl())
                        .post(body)
                        .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                        .build();


                try {
                    Response response = okHttpClient.newCall(request).execute();
                    assert response.body() != null;
                    // use Jackson to Decode the ResponseBody ...
                    //Inserting transaction in transaction table
                    Transaction TransObj = new Transaction();
                    var trans = TransObj.builder()
                            .transactionAmount(internalStkPushRequest.getAmount())
                            .transactionType("Deposit")
                            .ReferenceCode(HelperUtility.getTransactionUniqueNumber())
                            .Debited(internalStkPushRequest.getAmount())
                            .Credited(internalStkPushRequest.getAmount())
                            .Status("0")
                            .build();
                    transactionRepository.save(trans);
                    // Updating Accounts table
                    Account mtransactionAccount = accountRepository.findByAccountNumber(internalStkPushRequest.getPhoneNumber());
                    double currentAccountbBalance = mtransactionAccount.getAccountBalance();
                    UpdatedAccountBalance = currentAccountbBalance + internalStkPushRequest.getAmount();
                    mtransactionAccount.setAccountBalance(UpdatedAccountBalance);
                    mtransactionAccount.setBalanceBefore(currentAccountbBalance);
                    accountRepository.save(mtransactionAccount);

                    stkPushSyncResponse = objectMapper.readValue(response.body().string(), StkPushSyncResponse.class);
                } catch (IOException e) {
                    log.error(String.format("Could not perform the STK push request -> %s", e.getLocalizedMessage()));
                    return null;
                }
            }
            return stkPushSyncResponse;
        }

    @Override
    public B2CTransactionSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest) {


        AccessTokenResponse accessTokenResponse = getAccessToken();
        log.info(String.format("Access Token: %s", accessTokenResponse.getAccessToken()));

        B2CTransactionRequest b2CTransactionRequest = new B2CTransactionRequest();

        b2CTransactionRequest.setCommandID(mpesaConfiguration.getCommandID());
       // b2CTransactionRequest.setPartyA(internalB2CTransactionRequest.getPartyA());
        b2CTransactionRequest.setAmount(internalB2CTransactionRequest.getAmount());
        b2CTransactionRequest.setPartyB(internalB2CTransactionRequest.getPartyB());
        b2CTransactionRequest.setRemarks(internalB2CTransactionRequest.getRemarks());
        b2CTransactionRequest.setOccassion(internalB2CTransactionRequest.getOccassion());

        // get the security credentials ...
        b2CTransactionRequest.setSecurityCredential(HelperUtility.getSecurityCredentials(mpesaConfiguration.getB2cInitiatorPassword()));

        log.info(String.format("Security Creds: %s", b2CTransactionRequest.getSecurityCredential()));

        // set the result url ...
        b2CTransactionRequest.setResultURL(mpesaConfiguration.getB2cResultUrl());
        b2CTransactionRequest.setQueueTimeOutURL(mpesaConfiguration.getB2cQueueTimeoutUrl());
        b2CTransactionRequest.setInitiatorName(mpesaConfiguration.getB2cInitiatorName());
        b2CTransactionRequest.setPartyA(mpesaConfiguration.getShortCode());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(b2CTransactionRequest)));

        Request request = new Request.Builder()
                .url(mpesaConfiguration.getB2cTransactionEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            assert response.body() != null;

            return objectMapper.readValue(response.body().string(), B2CTransactionSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not perform B2C transaction ->%s", e.getLocalizedMessage()));
            return null;
        }

    }
}
