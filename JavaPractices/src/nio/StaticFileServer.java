package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticFileServer {
	private static final Logger LOGGER = Logger.getLogger(StaticFileServer.class.getName());
	private static final Pattern PATH_EXTRACTOR = Pattern.compile("GET (.*?) HTTP");
	private static final String INDEX_PAGE = "index.html";
	
	public void start(final Path root) throws IOException {
		AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, 
				Executors.defaultThreadFactory());
		final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group)
				.bind(new InetSocketAddress(10080));
		serverChannel.accept(null, 
				new CompletionHandler<AsynchronousSocketChannel, Void> () {
			public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
				serverChannel.accept(null, this);
				try {
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					clientChannel.read(buffer).get();
					buffer.flip();
					String request = new String(buffer.array());
					
					System.out.println("processing request:[" + request + "]");
					String requestPath = extractPath(request);
					System.out.println("requestPath:[" + requestPath + "]");
					
					Path filePath = getFilePath(root, requestPath);
					if(!Files.exists(filePath)) {
						return;
					}
					LOGGER.log(Level.INFO, "processing request:{0}", requestPath);
					String header = generateResponseHeader(filePath);
					clientChannel.write(ByteBuffer.wrap(header.getBytes())).get();
					Files.copy(filePath, Channels.newOutputStream(clientChannel));
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				} finally {
					try {
						clientChannel.close();
					} catch (IOException e) {
						LOGGER.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
			public void failed(Throwable throwable, Void attachment) {
				LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable);
			}
		});
		
		System.out.println("Server started");
	}
	
	private String extractPath(String request) {
		Matcher matcher = PATH_EXTRACTOR.matcher(request);
		if(matcher.find()) {
			return matcher.group(1);
		}
		System.out.println("No match");
		return null;
	}
	
	private Path getFilePath(Path root, String requestPath) {
		if(requestPath == null || "/".equals(requestPath)) {
			requestPath = INDEX_PAGE;
		}
		if(requestPath.startsWith("/")) {
			requestPath = requestPath.substring(1);
		}
		int pos = requestPath.indexOf("?");
		if(pos >= 0) {
			requestPath = requestPath.substring(0, pos);
		}
		return root.resolve(requestPath);
	}
	
	private String getContentType(Path filePath) throws IOException {
		return Files.probeContentType(filePath);
	}
	
	private String generateResponseHeader(Path filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 200 OK\r\n")
		  .append("Content-Type: ")
		  .append(getContentType(filePath))
		  .append("\r\n")
		  .append("Content-Length: " + Files.size(filePath) + "\r\n")
		  .append("\r\n");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		StaticFileServer server = new StaticFileServer();
		Path path = FileSystems.getDefault().getPath("D:",  "tmp");
		try {
			server.start(path);
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
