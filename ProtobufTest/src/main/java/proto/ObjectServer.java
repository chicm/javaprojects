package proto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.BlockingService;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import proto.generated.AddressBookProtos.Name;
import proto.generated.AddressBookProtos.Person;
import proto.generated.AddressBookProtos.PersonService;
import proto.generated.AddressBookProtos.PersonService.BlockingInterface;
import proto.generated.AddressBookProtos.RequestHeader;

public class ObjectServer extends Thread{
	private static PersonServiceHandler handle = new PersonServiceHandler();
  //private static BlockingService service = new PersonServiceHandler().getService();
  
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
			channel.configureBlocking(true);
			channel.socket().setReuseAddress(true);
			channel.socket().bind(new InetSocketAddress(9999));
			
			while(true) {
			  SocketChannel c = channel.accept();
			  process3(c);
			}
			//System.out.printf("SERVER THREAD[%s]:register\n", Thread.currentThread().getName() ); 
			
			
/*
			channel.register(selector, SelectionKey.OP_ACCEPT );
			
			while(selector.select() > 0 ) {
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext()) {
					SelectionKey k = it.next();
					it.remove();
					process(k);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace(System.out);
			
		}
	}
	public static void process2(SocketChannel channel) throws Exception {
	  ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(channel));
    Person p = (Person)ois.readObject();
    
    System.out.printf("SERVER THREAD[%s]:received:[%s]\n", Thread.currentThread().getName(), p.toString() ); 
   
	}
	public static void process3(final SocketChannel channel) throws Exception {
	  ByteBuffer buf = ByteBuffer.allocate(1000);
    Thread.sleep(1000);
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
        buf.flip();
        len = channel.read(buf);
        buf.position(offset);
        data = new byte[len];
        buf.get(data);
      }
      
      
      RequestHeader header = RequestHeader. newBuilder().mergeFrom(data, offset, headerSize ).build();
      System.out.println("server: header parsed:" + header.toString());
      
      if(len <= headerSize) {
        len = channel.read(buf);
        buf.position(offset+headerSize);
        data = new byte[len];
        buf.get(data);
      }
      offset += headerSize;
      /*
      buf.flip();
      data = new byte[len];
      
      buf.get(data);
      offset = 0;
      RequestHeader h = RequestHeader.parseFrom(data);
      offset = h.getSize();
      if(len <= offset) {
        //buf.flip();
        //buf.limit(1000);
        //buf = ByteBuffer.allocate(1000);
        buf.clear();
        int newlen = channel.read(buf);
        if(newlen <= 0) {
          System.out.println("no data");
          return;
        }
        data = new byte[newlen];
        buf.flip();
        buf.get(data);
        offset = 0;
        len = newlen;
      }*/
      MethodDescriptor md = handle.getService().getDescriptorForType().findMethodByName(header.getMethodName());
      Builder builder = handle.getService().getRequestPrototype(md).newBuilderForType();
      // To read the varint, I need an inputstream; might as well be a CIS.
      Message request = null;
      if (builder != null) {
        request = builder.mergeFrom(data, offset, len-offset).build();
        System.out.println("server : request parsed:" + request.toString());
        Message response = handle.getService().callBlockingMethod(md, null, request);
        System.out.println("server method called:" + header.getMethodName());
        
        System.out.println("Map:" + handle.getMap());
      }
      
      /*
      Person  p = Person.parseFrom(data);
      //String msg = new String(data);
      System.out.printf("SERVER THREAD[%s]:received:[%s]\n", Thread.currentThread().getName(), p.toString() );
      
      PersonServiceHandler h = new PersonServiceHandler();
      RpcCallback<Person> done = new RpcCallback<Person>() {
        public void run(Person p) {
          try {
            byte[] b = p.toByteArray();
            ByteBuffer buf = ByteBuffer.wrap(b);
            channel.write(buf);
          } catch(Exception e) {       
          }
        }
      };
      h.add(null, p, done);
      */
      System.out.println("server: add done");
    } else {
      channel.close();
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
				ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(socketChannel));
				Person p = (Person)ois.readObject();
				
				System.out.printf("SERVER THREAD[%s]:received:[%s]\n", Thread.currentThread().getName(), p.toString() ); 
        
				ois.close();
				//socketChannel.close();
				//socketChanne
				/*
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
				}*/
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
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
			threads[i] = new Thread(new Client(i+1, String.format("Person%01d", i+1)));
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
			
			
			/*
			RequestHeader.Builder builder = RequestHeader.newBuilder();
			builder.setCallId(100);
			builder.setMethodName("add");
			//builder.set
			RequestHeader h = builder.build();
			//service.add(null, h);
			*/
			
			/*
			Person p = AddPerson.newPerson(1, "Mike", "mike@aaa.com", "12345678");
			byte[] b = p.toByteArray();
      ByteBuffer buf = ByteBuffer.wrap(b);
      channel.write(buf);
			*/
			
			/*
			ObjectOutputStream	oos = new ObjectOutputStream(Channels.newOutputStream(channel));
      
			Person p = AddPerson.newPerson();
      oos.writeObject(p);
      oos.close();
      */
     // channel.close();
			
      /*
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
			}*/
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
    
    
    byte[] b1 = h.toByteArray();
    ByteBuffer buf1 = ByteBuffer.wrap(b1);
    
    byte[] b2 = param.toByteArray();
    ByteBuffer buf2 = ByteBuffer.wrap(b2);
    
    byte[] b3 = new byte[10];
    CodedOutputStream cos = CodedOutputStream.newInstance(b3);
    cos.writeRawVarint32(h.getSerializedSize());
    ByteBuffer buf3 = ByteBuffer.wrap(b3, 0, cos.computeRawVarint32Size(h.getSerializedSize()));
    
    System.out.println("buf1:" + buf1.capacity());
    System.out.println("buf2:" + buf2.capacity());
    System.out.println("h:" + h.getSerializedSize());
    System.out.println("param:" + param.getSerializedSize());
    
    //buf3.
    
      channel.write(buf3);
      channel.write(buf1);
      channel.write(buf2);
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