package nio;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class FullDuplexClient {

  private static String host = "localhost";
  private static int port = 12888;
  private static int PACKET_SIZE = 1024*8;
      
  private static  AsynchronousSocketChannel channel = null;
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    if(args.length < 3) {
      System.out.println("usage: FullDuplexClient <server host> <server port> <packet size> ");
      return;
    }
    host = args[0];
    port = Integer.parseInt(args[1]);
    PACKET_SIZE = Integer.parseInt(args[2]);
    connect();
  }
  
  public static void connect() {
  try  {
    InetSocketAddress isa = new InetSocketAddress(host, port);
    channel = AsynchronousSocketChannel.open();
    channel.setOption(StandardSocketOptions.SO_RCVBUF, PACKET_SIZE);
    channel.setOption(StandardSocketOptions.SO_SNDBUF, PACKET_SIZE);
    //channel.setOption(name, value)
    channel.connect(isa).get();
    System.out.println("client connected");
    startRead();
    long tm = System.currentTimeMillis();
    for(int i = 0; ; i++) {
      try {
        ByteBuffer buf = ByteBuffer.allocateDirect(PACKET_SIZE);
        byte[] b = new byte[PACKET_SIZE];
        buf.put(b);
        buf.flip();
        channel.write(buf).get();
        
        if(i % 1000 == 0) {
          long ms = System.currentTimeMillis() - tm;
          //long bps = (i * PACKET_SIZE / (1024*1024)) * 1000 / ms;
          System.out.println("client wrote: " + i + " package/sec: " + (i*1000/ms));
        }
        
        } catch (Exception e) {
          e.printStackTrace(System.out);
        }
    }
    
  
  } catch(Exception e) {
    e.printStackTrace(System.out);
  }
  }
  
  public static void startRead () {
    new Thread(new Runnable(){
      public void run() {
        long tm = System.currentTimeMillis();
        for(int i =0; ; i++) {
          try {
            ByteBuffer buf = ByteBuffer.allocateDirect(PACKET_SIZE);
            channel.read(buf).get();
          
          if(i % 100 == 0) {
            long ms = System.currentTimeMillis() - tm;
            //long bps = (i * PACKET_SIZE / (1024*1024)) * 1000 / ms;
            System.out.println("client read: " + i + " package/sec: " + (i*1000/ms));
          }
          
          } catch (Exception e) {
            e.printStackTrace(System.out);
          }
        }
      }
    }).start();
  }
}
