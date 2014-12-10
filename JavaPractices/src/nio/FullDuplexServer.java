package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FullDuplexServer {
  public static int SERVER_PORT = 12888;
  private final static int DEFAULT_RPC_LISTEN_THREADS = 1;
  private SocketListener socketListener = null;
  private int rpcListenThreads = DEFAULT_RPC_LISTEN_THREADS;
  public static int PACKET_SIZE = 1024*8;
  //public static int TCP_BUF = 1024*1024;
  
  private static AsynchronousSocketChannel channel = null;
  
  public static void main(String[] args) throws Exception {
    
    if(args.length < 2) {
      System.out.println("usage: FullDuplexClient <listen port> <packet size> ");
      return;
    }
    SERVER_PORT = Integer.parseInt(args[0]);
    PACKET_SIZE = Integer.parseInt(args[1]);
    
    FullDuplexServer server = new FullDuplexServer(10);
    server.startRpcServer();
    
    //Client.connect();
    
  }
  
  FullDuplexServer (int nListenThreads) {
    socketListener = new SocketListener();
    rpcListenThreads = nListenThreads;
  }
  
  public boolean startRpcServer() {
    try {
      socketListener.start();
    } catch(IOException e) {
      e.printStackTrace(System.out);
      return false;
    }
    return true;
  }
  public static void startWriteBack () {
    new Thread(new Runnable(){
      public void run() {
        long tm = System.currentTimeMillis();
        for(int i =0; ; i++) {
          try {
          ByteBuffer buf = ByteBuffer.allocateDirect(PACKET_SIZE);
          byte[] b = new byte[PACKET_SIZE];
          buf.put(b);
          buf.flip();
          channel.write(buf).get();
          
          if(i % 100 == 0) {
            
            long ms = System.currentTimeMillis() - tm;
            //long bps = (i * PACKET_SIZE / (1024*1024)) * 1000 / ms;
            System.out.println("server wrote back: " + i + " package/sec: " + (i*1000/ms));
            
          }
          
          } catch (Exception e) {
            e.printStackTrace(System.out);
          }
        }
      }
    }).start();
  }
  
  class SocketHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverChannel) {
      
      serverChannel.accept(serverChannel, this);
      FullDuplexServer.channel = channel;
      System.out.println("server accepted");
      
      
      FullDuplexServer.startWriteBack();
      long tm = System.currentTimeMillis();
      for(int i = 0; ; i++) {
        try {
          processRequest(channel);
          if(i % 1000 == 0) {
            long ms = System.currentTimeMillis() - tm;
            //long bps = (i * PACKET_SIZE / (1024*1024)) * 1000 / ms;
            System.out.println("server read: " + i + " package/sec: " + (i*1000/ms));
          }
        } catch(Exception e) {
          e.printStackTrace(System.out);
          try {
            System.out.println("server close");
            channel.close();
          } catch(Exception e2) {
          }
          break;
        } 
      }
    }
    @Override
    public void failed(Throwable throwable, AsynchronousServerSocketChannel attachment) {
      throwable.printStackTrace(System.out);
    }
  }
  
  class SocketListener {
    public void start() throws IOException {
      AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(rpcListenThreads, 
          Executors.defaultThreadFactory());
      final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group);
      serverChannel.bind(new InetSocketAddress(SERVER_PORT));
      
      serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, PACKET_SIZE);
      //serverChannel.setOption(StandardSocketOptions.SO_SNDBUF, TCP_BUF);
      serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      
      serverChannel.accept(serverChannel, new SocketHandler());
      System.out.println("server started");
    }
  }
  
  private int processRequest(AsynchronousSocketChannel channel) 
      throws InterruptedException, ExecutionException {
    int ret = -1;
    try {
      if(!channel.isOpen()) {
        System.out.println("ERROR: NOT OPEN");
        return -1;
      }
      ByteBuffer buf = ByteBuffer.allocateDirect(PACKET_SIZE);
      Future<Integer> f = channel.read(buf);
      ret = f.get();
      
      
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace(System.out);
      throw e;
    } catch(Exception e) {
      e.printStackTrace(System.out);
    } 
    return ret;
  }
}


class Client {
  private static  AsynchronousSocketChannel channel = null;
  public static void connect() {
  try  {
    InetSocketAddress isa = new InetSocketAddress("localhost", FullDuplexServer.SERVER_PORT);
    channel = AsynchronousSocketChannel.open();
    channel.setOption(StandardSocketOptions.SO_RCVBUF, FullDuplexServer.PACKET_SIZE);
    channel.setOption(StandardSocketOptions.SO_SNDBUF, FullDuplexServer.PACKET_SIZE);
    channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    channel.connect(isa).get();
    System.out.println("client connected");
    startRead();
    
    for(int i = 0; ; i++) {
      try {
        ByteBuffer buf = ByteBuffer.allocateDirect(FullDuplexServer.PACKET_SIZE);
        byte[] b = new byte[FullDuplexServer.PACKET_SIZE];
        buf.put(b);
        buf.flip();
        channel.write(buf).get();
        
        if(i % 1000 == 0) {
          System.out.println("client wrote: " + i);
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
        for(int i =0; ; i++) {
          try {
            ByteBuffer buf = ByteBuffer.allocateDirect(FullDuplexServer.PACKET_SIZE);
            channel.read(buf).get();
          
          if(i % 100 == 0) {
            System.out.println("client read: " + i);
          }
          
          } catch (Exception e) {
            e.printStackTrace(System.out);
          }
        }
      }
    }).start();
  }
  
}
