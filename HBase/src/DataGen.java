

import java.io.*;
import java.util.Random;
import java.util.UUID;

public class DataGen {
	//private static final String PATH = "C:/data.txt";
	 
    public static void main(String[] args) throws IOException {
    	
    	if(args.length < 2) {
    		System.out.println("usage: java DataGen <filename> <number of rows> <number of version>");
    		return;
    	}
    	
        long startTime = System.currentTimeMillis();
        String path = args[0];
        File dataFile = getFile(path);
        FileWriter writer = null;
        try {
            writer = new FileWriter(dataFile);
            int timeCount = 0;
            timeCount = Integer.parseInt(args[1]);
            int resourceCount = 1;
            if(args.length >=3)
            	resourceCount = Integer.parseInt(args[2]);
            
            for (int j = 0; j < timeCount; j++) {
                long timeStamp = System.currentTimeMillis();
                for (int i = 0; i < resourceCount; i++) {
                    UUID uuid = UUID.randomUUID();
                    String rowKey = uuid.toString() + "_" + timeStamp;
                    Random random = new Random();
                    String cpuLoad = String.valueOf(random.nextDouble())
                            .substring(0, 4);
                    String memory = String.valueOf(random.nextDouble())
                            .substring(0, 4);
                    StringBuilder builder = new StringBuilder();
                    builder.append(rowKey).append("\t").append(cpuLoad)
                            .append("\t").append(memory).append("\t").append(uuid.toString()).append("\t").append(timeStamp);
                    writer.append(builder.toString());
                    if ((i +  1) * (j + 1) < timeCount * resourceCount) {
                        writer.append("\n");
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Cost Time: " + (endTime - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
 
 
    private static File getFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
