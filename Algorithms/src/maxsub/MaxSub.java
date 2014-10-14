package maxsub;

public class MaxSub {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a[] = {-1, -2, 5, -6, 7, -2, 9, 6, -6};
		
		int maxsum = maxSub(a);
		System.out.println("maxsub:" + maxsum);
		
		System.out.println("naivemaxsub:" + maxsum);

	}
	
	public static int maxSub(int a[]) {
		int n = a.length;
		
		int maxsum = 0;
		int sum = 0;
		for (int i = 0; i < n; i++) {
			sum += a[i];
			if (sum > maxsum)
				maxsum = sum;
			if(sum < 0)
				sum = 0;
		}
		return maxsum;
	}
	
	public static int naiveMaxSub ( int a[]) {
		int n = a.length;
		int sum = 0;
		int maxsum = 0;
		
		for (int i =0; i < n ; i++) {
			for(int j = 0; j < i; j ++) {
				for (int k =j ;k <i; k++) {
					sum += a[k];
				}
				if(sum > maxsum)
					maxsum = sum;
			}
		}
		
		return maxsum;
	}

}
