package multithread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PrinterManager {
	private final Semaphore semaphore;
	private final List<Printer> printers = new ArrayList<> ();
	
	public PrinterManager(Collection< ? extends Printer> printers) {
		this.printers.addAll(printers);
		this.semaphore = new Semaphore(this.printers.size(), true);
	}
	public PrinterManager() {this.semaphore = new Semaphore(0, true);}
	
	public Printer acquirePrinter() throws InterruptedException {
		semaphore.acquire();
		return getAvailablePrinter();
	}
	
	public void releasePrinter (Printer printer) {
		semaphore.release();
		putBackPrinter(printer);
	}
	
	public void releasePrinter( int num) {
		semaphore.release(num);
		for(int i=0; i < num; i++) {
			putBackPrinter(new Printer());
		}
	}
	
	private synchronized Printer getAvailablePrinter() {
		Printer result = printers.get(0);
		printers.remove(0);
		return result;
	}
	private synchronized void putBackPrinter(Printer printer) {
		printers.add(printer);
	}

	class Producer implements Runnable {
		private PrinterManager pm ;
		public Producer(PrinterManager pm) {
			this.pm = pm;
		}
		public void run() {
			while (true) {
				pm.releasePrinter(5);
				System.out.println(Thread.currentThread().toString() + "Release 5 printers");
				try {
					Thread.sleep(3000);
				} catch(InterruptedException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}
	
	class Consumer implements Runnable {
		private PrinterManager pm ;
		public Consumer(PrinterManager pm) {
			this.pm = pm;
		}
		public void run() {
			while (true) {
				try {
					pm.acquirePrinter();
					System.out.println(Thread.currentThread().toString() + "Get printer");
					
				} catch(InterruptedException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}
	public static void main(String[] args) {
		PrinterManager pm = new PrinterManager (); 
		ExecutorService es = Executors.newFixedThreadPool(10);
	
		Producer p = pm.new Producer(pm);
		es.submit(p);
		
		Consumer[] c = new Consumer[8];
		for(Consumer i:c) {
			i = pm.new Consumer(pm);
			es.submit(i);
		}

		try {
			es.awaitTermination(10000, TimeUnit.MINUTES);
		} catch(InterruptedException e) {
			e.printStackTrace(System.out);
		}
	};
	
	class Printer {}
}
