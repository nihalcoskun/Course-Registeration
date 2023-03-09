package tr.edu.marmara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionReporter {
    private Logger logger = LogManager.getLogger(ActionReporter.class);

    public void print(String message) {
        System.out.println(message);
    }

    public void err(Exception e) {
        logger.error(e.getMessage());
    }
}
