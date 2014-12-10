package lambda;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Lambda {

	public static void distinctPrimary(Integer... numbers) {
        List<Integer> l = Arrays.asList(numbers);
        List<Integer> r = l.stream()
                .map(e -> new Integer(e))
                .filter(e -> { 
                	if(e == 1) return false;
                	for(int i=2; i<e; i++) {
                	if(e % i == 0) return false;
                	}
                	return true;}
                  )
                .distinct()
                .collect(Collectors.toList());
        System.out.println("distinctPrimary result is: " + r);
    }
	
	public static void test() {
		List<String> list = Arrays.asList("abc", "aaa", "ddd", "ccc");
		list.stream().forEach(System.out::println);
		list.stream().forEach(x->System.out.println(x));
		//list.forEach(Arrays::asList);
	}
	
	public static void test2() {
		Path p = Paths.get("e:/healthcheck/");
		try {
			Files.walk(p, FileVisitOption.FOLLOW_LINKS).forEach(System.out::println);
		} catch(IOException e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Lambda.distinctPrimary(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
		Lambda.test2();
	}

}
