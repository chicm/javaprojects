package proto;

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

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import proto.generated.AddressBookProtos.Person;
import proto.generated.AddressBookProtos.PersonService;
import proto.generated.AddressBookProtos.PersonService.BlockingInterface;
import proto.generated.AddressBookProtos.RequestHeader;

public class ObjectServer extends Thread{
	private static PersonServiceHandler handle = new PersonServiceHandler();
  
	public static void main(String[] args) {
	  ObjectServer server = new ObjectServer();
		server.start();
		
		Client.sendData();
	}
	
	public void run() {
	  ObjectServer.listen();
	}

	public static void listen() {
		try (Selector selector = Selector.open();
			ServerSocketChannel channel = ServerSocketChannel.open();) {
			channel.configureBlocking(false);
			channel.socket().setReuseAddress(true);
			channel.socket().bind(new InetSocketAddress(9999));
			
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
	        processRequest(socketChannel);

	      } catch(Exception e) {
	        e.printStackTrace(System.out);
	      }
	    }
	 }
	
	public static void processRequest(final SocketChannel channel) throws Exception {
	  ByteBuffer buf = ByteBuffer.allocate(1000);
	  int len = channel.read(buf); 
    
    if(len > 0) {
      int offset = 0;
      System.out.println("server received request, len:" + len);
      
      buf.flip();
      byte[] data = new byte[len];
      buf.get(data);
      
      CodedInputStream cis = CodedInputStream .newInstance(data, offset, len );
      int headerSize = cis.readRawVarint32();
      offset = cis.getTotalBytesRead();
      
      System.out.println("server: headersize:" + headerSize);
      
      if(len <= offset) {
        buf.clear();
        len = channel.read(buf);
        data = new byte[len];
        buf.flip();
        buf.get(data);
        offset=0;
      }  
      
      RequestHeader header = RequestHeader. newBuilder().mergeFrom(data, offset, headerSize ).build();
      System.out.println("server: header parsed:" + header.toString());
      
      offset += headerSize;
      if(len <= headerSize) {
        buf.clear();
        len = channel.read(buf);
        buf.flip();
        data = new byte[len];
        buf.get(data);
        offset=0;
      }

      MethodDescriptor md = handle.getService().getDescriptorForType().findMethodByName(header.getMethodName());
      Builder builder = handle.getService().getRequestPrototype(md).newBuilderForType();
      Message request = null;
      if (builder != null) {
        request = builder.mergeFrom(data, offset, len-offset).build();
        System.out.println("server : request parsed:" + request.toString());
        Message response = handle.getService().callBlockingMethod(md, null, request);
        System.out.println("server method called:" + header.getMethodName());
        
        System.out.println("Map:" + handle.getMap());
      }
 
      System.out.println("server: add done");
    } else {
      channel.close();
    }
	}
}

class Client implements Runnable {
	private static final int NTHREADS = 5;
	
	private int id = 0;
	private String name = "";
	
	public Client(int id, String name) {
	  this.id = id;
	  this.name = name;
	}
	    
	public static void sendData() {
		ExecutorService es = Executors.newFixedThreadPool(NTHREADS);
		Thread[] threads = new Thread[NTHREADS];
		for(int i = 0; i < NTHREADS; i++) {
			threads[i] = new Thread(new Client(i+1, String.format("Person%02d", i+1)));
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
			BlockingRpcChannel c = new BlockingRpcChannelImplementation(this, channel);
			BlockingInterface service =  PersonService.newBlockingStub(c);
			Person p = AddPerson.newPerson(id,  name, String.format("%s@aaa.com", name), "11111111");
			service.add(null, p);
		
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public BlockingRpcChannel createBlockingRpcChannel(SocketChannel channel) {
    return new BlockingRpcChannelImplementation(this, channel);
  }
	
	public Message callBlockingMethod(MethodDescriptor md, RpcController controller,
      Message param, Message returnType, SocketChannel channel) {
	  
	  Message respond = null;
	  try {
	  RequestHeader.Builder builder = RequestHeader.newBuilder();
    builder.setCallId(id); 
    builder.setMethodName(md.getName());
    
    RequestHeader h = builder.build();
    
    ByteBuffer buf = ByteBuffer.allocate(1000);
    
    byte[] b1 = h.toByteArray();
    
    ByteBuffer buf1 = ByteBuffer.wrap(b1);
    
    byte[] b2 = param.toByteArray();
    ByteBuffer buf2 = ByteBuffer.wrap(b2);
    
    byte[] b3 = new byte[10];
    CodedOutputStream cos = CodedOutputStream.newInstance(b3);
    cos.writeRawVarint32(h.getSerializedSize());
    ByteBuffer buf3 = ByteBuffer.wrap(b3, 0, cos.computeRawVarint32Size(h.getSerializedSize()));
    
    buf.put(b3, 0, cos.computeRawVarint32Size(h.getSerializedSize()));
    buf.put(b1);
    buf.put(b2);
    
    buf.flip();
    
    System.out.println("buf1:" + buf1.capacity());
    System.out.println("buf2:" + buf2.capacity());
    System.out.println("h:" + h.getSerializedSize());
    System.out.println("param:" + param.getSerializedSize());
    
    //buf3.
    /*
      channel.write(buf3);
      channel.write(buf1);
      channel.write(buf2);*/
    channel.write(buf);
      //channel.
    } catch(Exception e) {
      e.printStackTrace(System.out);
    }
	  
	  return respond;
	}

  /**
   * Blocking rpc channel that goes via hbase rpc.
   */
  public static class BlockingRpcChannelImplementation implements BlockingRpcChannel {
    //private final InetSocketAddress isa;
    private Client rpcClient = null;
    private SocketChannel channel = null;

    /**
     * @param defaultOperationTimeout - the default timeout when no timeout is given
     *                                   by the caller.
     */
    protected BlockingRpcChannelImplementation(Client rpcClient, SocketChannel channel) {
      //this.isa = new InetSocketAddress(sn.getHostname(), sn.getPort());
      this.rpcClient = rpcClient;
      this.channel = channel;
    }

    @Override
    public Message callBlockingMethod(MethodDescriptor md, RpcController controller,
                                      Message param, Message returnType) throws ServiceException {

      return this.rpcClient.callBlockingMethod(md, controller, param, returnType, this.channel);
    }
  }
}