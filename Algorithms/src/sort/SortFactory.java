package sort;

public class SortFactory {
	public Sort createInstance(String sortclassname) {
		switch (sortclassname) {
		case "QuickSort":
			return new QuickSort();
		case "InsertionSort":
			return new InsertionSort();
		case "MergeSort":
			return new MergeSort();
		case "HeapSort":
			return new HeapSort();
		}
		
		return null;
	}
}
