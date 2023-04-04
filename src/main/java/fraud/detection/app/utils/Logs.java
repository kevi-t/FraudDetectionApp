package fraud.detection.app.utils;

import fraud.detection.app.configurations.TwilioInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs {
    private final static Logger LOGGER= LoggerFactory.getLogger(TwilioInitializer.class);
    void log(String s){LOGGER.info(s);}
}
