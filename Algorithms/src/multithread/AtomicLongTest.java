package multithread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTest {
  private static int THREADS = 20;
  private static final AtomicLong counter = new AtomicLong(0);
  private static volatile boolean stop = false;
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    ExecutorService service = Executors.newFixedThreadPool(THREADS);
    for (int i = 0; i < THREADS; i++ ) {
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          long starttm = System.currentTimeMillis();
          while(!stop) {
            counter.getAndIncrement();
          }
          System.out.println("Thread time:" + (System.currentTimeMillis()-starttm));
        }
      });
      t.setDaemon(true);
      service.execute(t);
    }
    try {
      Thread.sleep(10000);
    } catch(Exception e) {
      e.printStackTrace(System.out);
    }
    stop = true;
    service.shutdown();
    System.out.println("counter:" + counter);
    
    
  }

}
