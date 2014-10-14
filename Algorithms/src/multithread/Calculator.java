package multithread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Calculator extends RecursiveTask <Integer> {
	
	public final static int THRESHHOLD=100;
	private int start;
	private int end;
	
	public Calculator(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	protected Integer compute() {
		int sum = 0;
		if(end - start < THRESHHOLD) {
			for(int i = start; i <=end; i++)
				sum += i;
			return sum;
		}
		
		int middle = (end + start) /2;
		
		Calculator left = new Calculator(start, middle);
		Calculator right = new Calculator(middle+1, end);
		
		left.fork();
		right.fork();
		
		sum = left.join() + right.join();
		
		return sum;
		
	}

	public static void main(String[] args) {
		Calculator c = new Calculator(1, 10000);
		ForkJoinPool p = new ForkJoinPool();
		//Future<Integer> f = p.submit(c);
		Integer n = p.invoke(c);
		
		System.out.println(n);
		

	}

}
