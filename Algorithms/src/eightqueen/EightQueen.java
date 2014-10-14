package eightqueen;

public class EightQueen {
	
	private int numFalse = 0;
	private int numTrue = 0;
	
	private void printArray (int[] array) {
		for (int n: array)
			System.out.print(n + " ");
		System.out.println("");
	}
	
	private boolean check(int[] array) {
		int len = array.length;
		for(int i = 0; i < len; i++) {
			for(int j = 0; j < i; j++) {
				if(( i - j == array[i] - array[j]) || ( i - j == array[j] - array[i])) {
					//System.out.println("i:" + i + " j:" + j);
					//System.out.println("a[i]:" + array[i] + " a[j]:" + array[j]);
					numFalse++;
					return false;
				}
			}
		}
		numTrue++;
		return true;
	}
	
	private void permutation(int[] array, int start, int end) {
		if(start == end) {
			if(check(array))
				printArray(array);
			return;
		}
		
		for (int i = start; i <=end; i++) {
			int t = array[start];
			array[start] = array[i];
			array[i] = t;
			
			permutation(array, start+1, end );
			
			t = array[start];
			array[start] = array[i];
			array[i] = t;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = {0, 1, 2, 3, 4, 5,6,7};
		EightQueen q = new EightQueen();
		q.permutation(a, 0, a.length-1);
		
		System.out.println("numfalse:" + q.numFalse);
		System.out.println("numtrue:" + q.numTrue);
	}

}
