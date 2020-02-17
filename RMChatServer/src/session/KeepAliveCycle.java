package session;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


/**
 * Ein Thread, um zu überprüfen, welche Nutzer sich zu lange nicht beim Server gemeldet haben.
 * Ist dies der Fall, gilt der User als offline und die Session wird aus der Session-Liste entfernt
 */
public class KeepAliveCycle implements Runnable  {
    private static final Logger logger = Logger.getLogger("logger");

    @Override
    public void run() {
        logger.info("KeepAliveCycle started");
        while(true){
            try {
                SessionHandler.checkAlives();
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
