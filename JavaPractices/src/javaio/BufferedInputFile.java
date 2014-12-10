package javaio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BufferedInputFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			read4("d:\\footer2.htm");
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static void read(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String s;
		StringBuilder sb = new StringBuilder();
		while ((s = in.readLine()) != null) {
			sb.append(s+ "\n");
		}
		in.close();
		
		System.out.println(sb);
	}
	
	public static void read2(String filename) throws IOException {
		FileInputStream in = new FileInputStream(filename);
		BufferedInputStream bin = new BufferedInputStream(in);
		int len = 0;
		byte buff[] = new byte[1024];
		while((len = bin.read(buff)) >0) {
			String s = new String(buff, 0, len);
			System.out.println(s);
		}
	}
	
	// BufferedRead needs to implement AutoClosable interface
	public static void read3(String filename) throws IOException {
		try (BufferedReader rd = new BufferedReader(new FileReader(filename))) {
			StringBuilder sb = new StringBuilder();
			String s;
			while((s = rd.readLine()) != null) {
				sb.append(s+"\n");
			}
			System.out.println(sb);
		} 
	}
	
	public static void read4(String filename) throws IOException {
		Files.lines(Paths.get(filename)).forEach(System.out::println);
	}

}
