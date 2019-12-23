package sessionHandler;

import java.util.concurrent.TimeUnit;

public class KeepAliveCycle implements Runnable  {

    @Override
    public void run() {
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
