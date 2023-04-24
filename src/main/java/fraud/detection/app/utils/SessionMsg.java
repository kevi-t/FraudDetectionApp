package fraud.detection.app.utils;

import fraud.detection.app.responses.UniversalResponse;

public class SessionMsg {
    public UniversalResponse getSessionErrorMsg(){
        return UniversalResponse.builder().status("1").message("Session time Expired").build();
    }
}
