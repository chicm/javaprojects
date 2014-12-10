package nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloWorldServer extends Thread{
	
	public static void main(String[] args) {
		HelloWorldServer server = new HelloWorldServer();
		server.start();
		
		HelloWorldClient.sendData();
	}
	
	public void run() {
		HelloWorldServer.listen();
	}

	public static void listen() {
		try (Selector selector = Selector.open();
			ServerSocketChannel channel = ServerSocketChannel.open();) {
			channel.configureBlocking(false);
			channel.socket().setReuseAddress(true);
			channel.socket().bind(new InetSocketAddress(9999));
			
			System.out.printf("SERVER THREAD[%s]:register\n", Thread.currentThread().getName() ); 

			channel.register(selector, SelectionKey.OP_ACCEPT );
			
			while(selector.select() > 0 ) {
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext()) {
					SelectionKey k = it.next();
					it.remove();
					process(k);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			
		}
	}
	
	public static void process(SelectionKey key) {
		if(key.isAcceptable()) {
			try {
				ServerSocketChannel server = (ServerSocketChannel)key.channel();
				SocketChannel channel = server.accept();
				channel.configureBlocking(false);
				channel.register(key.selector(), SelectionKey.OP_READ);
				
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
		} else if(key.isReadable()) {
			try {
				SocketChannel socketChannel = (SocketChannel)key.channel();
				ByteBuffer buf = ByteBuffer.allocate(100);
				int len = socketChannel.read(buf);
				if(len > 0) {
					buf.flip();
					byte[] data = new byte[len];
					buf.get(data);
					String msg = new String(data);
					System.out.printf("SERVER THREAD[%s]:received:[%s]\n", Thread.currentThread().getName(), msg ); 
				} else {
					socketChannel.close();
				}
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}
}

class HelloWorldClient implements Runnable {
	private static final int NTHREADS = 50;
	
	public static void sendData() {
		ExecutorService es = Executors.newFixedThreadPool(NTHREADS);
		Thread[] threads = new Thread[50];
		for(int i = 0; i < NTHREADS; i++) {
			threads[i] = new Thread(new HelloWorldClient());
			threads[i].setName(String.format("CLIENT%02d", i));
			es.submit(threads[i]);
			
		}
		try {
			es.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			
		}
	}
	
	public void run() {
		try (SocketChannel channel = SocketChannel.open()) {
			SocketAddress adr = new InetSocketAddress("localhost", 9999);
			channel.connect(adr);
			
			for(int i =0; i< 1; i++) {
			//Thread.currentThread().setName(String.format("CLIENT%02d", i));
			String str = Thread.currentThread().getName() + " Hello";
			byte[] bytes = str.getBytes();
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			channel.write(buf);
			
			String str2 = Thread.currentThread().getName() + " World";  
			byte[] bytes2 = str2.getBytes();
			ByteBuffer buf2 = ByteBuffer.wrap(bytes2);
			channel.write(buf2);
			}
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
}