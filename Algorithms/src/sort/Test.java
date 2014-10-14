package sort;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SortFactory f = new SortFactory();
		Sort s = f.createInstance("HeapSort");
		int a[] = {10, 2, 8, 9, 100, 3, 200, 6, 6, 6};

		s.sort(a);
		for(int i: a) {
			System.out.print(i + " ");
		}
		System.out.println("");
	}

}
