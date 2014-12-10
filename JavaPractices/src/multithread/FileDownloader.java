package multithread;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.*;


public class FileDownloader {
	private final ExecutorService es = Executors.newFixedThreadPool(10);
	private int threadid = 1;
	public boolean download(final URL url, final Path path) {
		Future<Path> future = es.submit(new Callable<Path>() {
			public Path call() {
				int id = threadid++;
				System.out.println("threadid:"  + id); 
				try (InputStream in = url.openStream()) {
					Files.copy(in,  path, StandardCopyOption.REPLACE_EXISTING);
					System.out.println("threadid:"  + id + " done"); 
					return path;
					
				} catch(IOException e) {
					System.out.println(e);
					System.out.println("threadid:"  + id + " done"); 
					return null;
				}
			}
		});
		
		return true;
		/*
		try {
			return future.get() != null ? true : false;
		} catch(InterruptedException | ExecutionException e) {
			return false;
		}*/
	}
	public void close() {
		es.shutdown();
		try {
			if(!es.awaitTermination(3, TimeUnit.MINUTES)) {
				es.shutdownNow();
				es.awaitTermination(1,  TimeUnit.MINUTES);
			}
		} catch(InterruptedException e) {
			es.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Path path = FileSystems.getDefault().getPath("D:",  "tmp", "f1.txt");
		FileDownloader fd = new FileDownloader();
		try{
			for(int i=0; i< 10; i++) {
				URL url = new URL("http://www.baidu.com");
				fd.download(url, path);
			}
			fd.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

}
