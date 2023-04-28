package fraud.detection.app.services;
import fraud.detection.app.dto.mpesa.*;
import fraud.detection.app.responses.UniversalResponse;

import java.io.IOException;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    AccessTokenResponse registerUrl() throws IOException;
    UniversalResponse DepositStkPushTransaction(InternalStkPushRequest internalStkPushRequest);
    B2CTransactionSyncResponse performB2CTransaction(InternalB2CTransactionRequest internalB2CTransactionRequest);
}
