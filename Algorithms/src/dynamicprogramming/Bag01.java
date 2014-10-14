package dynamicprogramming;



public class Bag01 {
	class Item {
		private int weight;
		private int value;
		public Item(int w, int v) {
			weight = w;
			value = v;
		};
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Bag01 bag = new Bag01();
		Item[] items= {bag.new Item(7, 18), bag.new Item(5, 10), bag.new Item(5, 10)};
		
		int value = bag.maxValue(items, 10);
		System.out.println("maxvalue:" + value);
	}
	
	private void printMatrix(int[][] matrix, int nRow, int nColumn) {
		for(int i = 0; i<nRow; i++) {
			for(int j = 0; j<nColumn; j++) {
				System.out.printf("%5d", matrix[i][j]);
			}
			System.out.println("");
		}
		System.out.println("***************************************");
	}
	
	private int maxValue(Item[] items, int weight) {
		int[][] valueMatrix = new int[items.length][weight+1];
		
		for(int j = 0; j <= weight; j++) {
			for (int i = 0; i < items.length; i++) {
				
				if(i == 0) {
					if(j >= items[i].weight)
						valueMatrix[i][j] = items[i].value;
					else
						valueMatrix[i][j] = 0;
					
					printMatrix(valueMatrix, items.length, weight+1);
					continue;
				}
				int max = valueMatrix[i-1][j];
				if(j >= items[i].weight) {
					if(valueMatrix[i-1][j-items[i].weight] + items[i].value > max)
						max = valueMatrix[i-1][j-items[i].weight] + items[i].value;
				}
				valueMatrix[i][j] = max;
				printMatrix(valueMatrix, items.length, weight+1);
			}
		}
		return valueMatrix [items.length -1 ][weight];
	}

}
