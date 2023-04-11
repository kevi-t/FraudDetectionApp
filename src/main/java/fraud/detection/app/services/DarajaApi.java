package fraud.detection.app.services;
import fraud.detection.app.dto.AccessTokenResponse;
import fraud.detection.app.dto.InternalStkPushRequest;
import fraud.detection.app.dto.StkPushSyncResponse;
import okhttp3.Response;

import java.io.IOException;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    AccessTokenResponse registerUrl() throws IOException;
    StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest);

}
