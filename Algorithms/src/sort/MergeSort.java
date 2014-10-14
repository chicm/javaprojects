package sort;

public class MergeSort implements Sort {
	public void sort(int[] a) {
		int n = a.length;
		if( n < 2)
			return;
		mergesort(a, 0, n-1);
	}
	
	private void mergesort(int[] a, int start, int end) {
		
		int middle = (start+end) /2;
		
		if(middle > start)
			mergesort(a, start, middle);
		if(end > middle +1)
			mergesort(a, middle+1, end);
		
		merge(a, start, middle, a, middle+1, end, a, start);
		
	}
	
	private void merge(int[] a, int start1, int end1, int[] b, int start2, int end2, int[] out, int pos) {
		int len1 = end1 - start1 +1;
		int len2 = end2 - start2 +1;
		
		int tmpout[] = new int[len1 + len2];
		int i,j,k;
		for(i = start1, j = start2, k=0 ; i <= end1 && j<=end2;) {
			if(a[i] < b[j])
				tmpout[k++] = a[i++];
			else
				tmpout[k++] = b[j++];
		}
		while(i <=end1)
			tmpout[k++] = a[i++];
		while(j <=end2)
			tmpout[k++] = b[j++];
		
		System.arraycopy(tmpout, 0, out, pos, tmpout.length);
		
	}
}
