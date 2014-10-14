package sort;

public class QuickSort implements Sort {

	private static int partitionid = 1; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a[] = {2, 8, 7, 1, 3, 5, 6, 4};
		QuickSort s = new QuickSort();
		s.sort(a);
		for(int i: a) {
			System.out.print(" " + i);
		}
		System.out.println("");

	}
	
	public void sort(int [] a) {
		quicksort(a, 0, a.length-1);
	}
	
	private void quicksort(int [] a, int begin, int end) {
		if(begin == end )
			return;
		int n = partition2(a, begin, end);
		if ( n -1 >= begin)
			quicksort(a, begin, n-1);
		if(n <= end)
			quicksort(a, n+1, end);
	}
	
	private void swap(int[] a, int i, int j) {
		int tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}
	
	private int partition2 (int[] a, int begin, int end) {
		if(begin >= end)
			return begin;
		
		int pivot = a[end];
		int i = begin;
		for(int j = begin; j <= end-1; j++) {
			if(a[j] <= pivot) {
				swap(a, i++, j);
			}
		}
		swap (a, i, end);
		return i;	
	}
	
	private int partition (int[] a, int begin, int end) {
		
		if (begin >= end )
			return begin;
		
		System.out.println("*****************************");
		System.out.println("partition " + (partitionid++));
		System.out.println("a:");
		for(int n: a) {
			System.out.print(" " + n);
		}
		System.out.println("");
		System.out.println("pivot=" + a[begin]);
		System.out.println("begin:" + begin);
		System.out.println("end:" + end);
		
		
		int i = begin;
		int j = end;
		int pivot = a[begin];
		
		for (;;) {
			while(j > begin && a[j] >= pivot)
				j--;
			while(i < end && a[i] <= pivot)
				i++;
			
			if(j <= i) {
				a[begin] = a[j];
				a[j] = pivot;
				break;
			}
			
			if (a[j] < a[i]) {
				int t = a[i];
				a[i++] = a[j];
				a[j--] = t;
			}
			
		}
		
		System.out.println("partition done");
		System.out.println("a:");
		for(int n: a) {
			System.out.print(" " + n);
		}
		System.out.println("");
		System.out.println("returned j =" + j);
		System.out.println("*****************************");
		
		return j;
	}

}
