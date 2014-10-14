import java.util.concurrent.*;

class MyThread extends Thread {
	public void run () {
		for (;;) {
			System.out.println ("running");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class MyRunnable implements Runnable {
	private long countuntil = 0;
	
	MyRunnable(long countuntil) {
		this.countuntil = countuntil;
	}
	
	public void run () {
		long sum = 0;
		for (long i = 1 ; i < countuntil; i++)
			sum += i;
		System.out.println(sum);
	}
}

class MyCallable implements Callable<Long> {
	public Long call() throws Exception {
		long sum = 0;
	    for (long i = 0; i <= 100; i++) {
	      sum += i;
	    }
	    return sum;
	}
}

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//testStop();
		//testCallable();
		testExecutor();
	}
	
	public static void testExecutor() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 100; i ++) {
			Runnable r = (Runnable) new MyRunnable(10000000L + i);
			executor.execute(r); 
			//executor.
		}
		
		executor.shutdown();
		System.out.println("shutdown");
		executor.awaitTermination(60, TimeUnit.SECONDS);
		System.out.println("end");
	}
	
	public static void testStop() throws InterruptedException {
		MyThread t = new MyThread() ;
		t.start();
		Thread.sleep(1000);
		t.stop();
		System.out.println("stopped.");
	}

	public static void testCallable() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable c = new MyCallable();
		Future<Long> f = executor.submit(c);
		System.out.println(f.get());

	}
}
