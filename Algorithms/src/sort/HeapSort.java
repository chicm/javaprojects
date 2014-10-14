package sort;

public class HeapSort implements Sort {
	
	public static void main(String[] args) {
		HeapSort s = new HeapSort();
		int[] a = { 1000, 200, 1, 3, 5, 2000, 3, 20, 15};
		s.sort(a);
		for( int i: a)
			System.out.print(i + " ");
	}
	
	public void sort(int[] a) {
		if(a.length < 2)
			return;
		buildMaxHeap(a);
		for(int i = a.length-1; i>0; i--) {
			int tmp = a[0];
			a[0] = a[i];
			a[i] = tmp;
			maxHeapify(a, i, 0);
		}
	}
	
	private void buildMaxHeap (int[] a) {
		if(a.length < 2 )
			return;
		
		for(int i = a.length /2 -1; i >= 0; i--) {
			maxHeapify(a, a.length, i);
		}
	}
	
	private void maxHeapify(int[] a, int len, int root) {
		if(len <= 1)
			return;
		
		int left = root * 2 + 1;
		int right = root * 2 + 2;
		
		if(left > len -1)
			return;
		int largest = root;
		if(a[left] > a[root])
			largest = left;
		if(right <= len -1 && a[right] > a[largest])
			largest = right;
		if(largest != root) {
			int tmp = a[root];
			a[root] = a[largest];
			a[largest] = tmp;
			maxHeapify(a, len, largest);
		}
	}

}
