package session;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
