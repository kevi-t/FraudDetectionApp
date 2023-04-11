package fraud.detection.app.services;
import fraud.detection.app.dto.AccessTokenResponse;
import fraud.detection.app.dto.RegisterUrlResponse;

public interface DarajaApi {

    /**
     * @return Returns Daraja API Access Token Response
     */
    AccessTokenResponse getAccessToken();
    RegisterUrlResponse registerUrl();

}
