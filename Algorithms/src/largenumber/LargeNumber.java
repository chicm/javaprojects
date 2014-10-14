package largenumber;

public class LargeNumber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LargeNumber.multiply("666668888888888888888888888888888888888888888888888886", 
				"4567888888888888888888888888888888888888888888881");

	}

	public static void multiply(String num1, String num2) {
		int[] int1 = new int[num1.length()];
		int[] int2 = new int[num2.length()];
		
		for ( int i = 0; i < num1.length(); i++) {
			int1[i] = //Integer.parseInt(new String("" + num1.charAt(i)));
					num1.charAt(i) - '0';
		}
		for ( int i = 0; i < num2.length(); i++) {
			int2[i] = //Integer.parseInt(new String("" + num2.charAt(i)));
					num2.charAt(i) - '0';
		}
		int len1 = num1.length();
		int len2 = num2.length();
		
		int[] result = new int[len1 + len2];
		for(int i = 0; i < len1; i++) {
			for(int j = 0; j < len2; j++) {
				result[len1 + len2- (i+j) -1] += int1[len1-1-i] * int2[len2-1-j];
			}
		}
		
		for(int i = len1+len2-1; i >=0; i--) {
			int cur = result[i] % 10;
			int tmp = result[i] / 10;
			for(int j = 1; ; j++) {
				if(tmp == 0)
					break;
				result[i-j] += (tmp % 10);
				tmp = tmp/10;
			}
			result[i] = cur;
		}
		
		System.out.println("result:");
		for(int i : result) {
			System.out.print(i);
		}
	}
}
