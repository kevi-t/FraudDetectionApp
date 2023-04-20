package fraud.detection.app.utils;

import fraud.detection.app.configurations.TwilioInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Logs {
    private final static Logger LOGGER= LoggerFactory.getLogger(TwilioInitializer.class);
    public void log(String s){LOGGER.info(s);}
}
