package sort;

public class InsertionSort implements Sort {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void sort(int a[]) {
		int n = a.length;
		if(n < 2)
			return;
		
		for (int i = 1; i < n; i++ ) {
			
			int k = a[i];
			int j;
			for (j = i-1; j >=0 && a[j] >k; j--) {
				a[j + 1] = a[j];		
				
			}
			a[j+1] = k;
		}
	}

}
