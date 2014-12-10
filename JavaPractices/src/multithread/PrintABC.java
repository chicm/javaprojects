package multithread;

public class PrintABC extends Thread {
	private String s;
	private Object sync;
	private volatile boolean stop = false;
	
	public PrintABC(String c, Object sync) {
		s = c;
		this.sync = sync;
	}
	
	public void run() {
		while(true) {
			synchronized(sync) {
				try {
					if(stop)
						return;
					sync.wait();
				}
				catch (InterruptedException e) {
					//e.printStackTrace(System.out);
					return;
				}
				System.out.println(s);
				
				sync.notifyAll();
			}
			
		}
	}
	
	public void close() {
		stop = true;
	}
	
	public static void main(String[] args) {
		PrintABC[] threads = new PrintABC[3];
		Object[] syncObjs = new Object[3];
		for (int i = 0; i < 3; i++)
			syncObjs[i] = new Object();
		
		threads[0] = new PrintABC("A", syncObjs[0]);
		threads[1] = new PrintABC("B", syncObjs[1]);
		threads[2] = new PrintABC("C", syncObjs[2]);
		
		for(PrintABC t: threads)
			t.start();
		for(int i = 0; i<30; i ++) {
			synchronized(syncObjs[i%3]) {
				syncObjs[i%3].notifyAll();
				
				try {
					syncObjs[i%3].wait();
				} catch(InterruptedException e) {
					
				}
			}
		}
		
		for(PrintABC t: threads) {
			t.close();
			t.interrupt();
		}
	}

}
