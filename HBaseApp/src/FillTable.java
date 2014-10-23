import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;

//java -classpath $(hbase classpath) FillTable test1 10000000

public class FillTable implements Runnable{
	
	private static Configuration conf;
	private static HashMap<Integer, String> map;
	private static final int CACHE_NUMBER = 10000;
	private static final int NUM_REGIONS = 6;
	private int start =0;
	private int end = 0;
	private static String tableName = "";
	
	FillTable(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	static {
		conf = HBaseConfiguration.create();
		map = new HashMap<Integer, String>();
		for(int i = 0; i < 26; i++) {
			char[] c = new char[90];
			Arrays.fill(c, (char)('A' + i));
			map.put(i, new String(c));
		}
		for(int i = 0; i < 26; i++) {
			char[] c = new char[90];
			Arrays.fill(c, (char)('a' + i));
			map.put(i+26, new String(c));
		}
	}

	public static void main(String[] args) {
		//String tableName = "test";
		if(args.length > 0)
			tableName = args[0];
		int num = 100;
		if(args.length > 1)
			num = Integer.parseInt(args[1]);
		long starttime = System.currentTimeMillis();
		createTable(tableName, num);
		
		ExecutorService es = Executors.newFixedThreadPool(NUM_REGIONS);
		FillTable[] threads = new FillTable[NUM_REGIONS];
		int startkey = 0, endkey = num/NUM_REGIONS;
		for(int i = 0; i < NUM_REGIONS; i++) {
			threads[i]= new FillTable(startkey, endkey);
			startkey = endkey +1;
			endkey += num/NUM_REGIONS;
			if(endkey >= num || num - endkey < 2)
				endkey = num-1;
			
			es.submit(threads[i]);
		}
		es.shutdown();
		try {
			es.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES);
		} catch(InterruptedException e) {
			e.printStackTrace(System.out);
		}
		
		long endtime = System.currentTimeMillis();
		System.out.printf("total: %d seconds\n", (endtime - starttime)/1000);
	}
	
	public void run() {
		insertData(tableName, start, end);		
	}
	
	public static void createTable(String tableName, int num) {
		TableName table = TableName.valueOf(tableName);
		try {
			HBaseAdmin admin = new HBaseAdmin(conf);
			if(admin.tableExists(table)) {
				admin.disableTable(tableName);
				admin.deleteTable(table);
				System.out.printf("%s exists, deleting...\n", tableName);
			}
			HTableDescriptor tableDesc = new HTableDescriptor(table);
			tableDesc.addFamily(new HColumnDescriptor("f1"));
			//admin.createTable(tableDesc, genData(0).getBytes(), genData(num-1).getBytes(), NUM_REGIONS);
			admin.createTable(tableDesc, getSplits(num));
			System.out.printf("table %s created\n", tableName);
			admin.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static byte[][] getSplits(int num) {
		int key = num / NUM_REGIONS;
		System.out.println("splits:");
		byte[][] splits = new byte[NUM_REGIONS-1][];
		for(int i = 0; i < NUM_REGIONS-1; i++) {
			byte[] split = genKey(key).getBytes();
			System.out.println(genKey(key));
			splits[i] = split;
			key += num / NUM_REGIONS;
			
		}
		return splits;
	}
	
	public static void insertData(String tableName, int startKey, int endKey) {
		System.out.println("start insert data");
		
		try( HConnection connection = HConnectionManager.createConnection(conf);
			HTableInterface table = connection.getTable(tableName)) {
			table.setAutoFlush(false); 
		    // Use the table as needed, for a single operation and a single thread
			HRegionLocation region = connection.getRegionLocation(TableName.valueOf(tableName), genData(startKey).getBytes(), true);
			String id = String.format("[Thread-%d:%s:%s]", Thread.currentThread().getId(), region.getHostname(), region.getServerName().getServerName());
			List<Put> list = new ArrayList<Put>(CACHE_NUMBER);
			
			for(int i = startKey; i <= endKey; i++) {
				Put put = new Put(genKey(i).getBytes());
				put.setDurability(Durability.SKIP_WAL);
				put.add("f1".getBytes(), "a".getBytes(), genData(i).getBytes());
				table.put(put);
				/* remove cache
				list.add(put);
				
				if(i % 100000 == 0) {
					System.out.printf("%scount:%d\n",id, i);
				}
				
				if( (i!=startKey && (i-startKey+1) % CACHE_NUMBER == 0) || i == endKey) {
					System.out.printf("***%sPUT: n=%d, list.size=%d\n", id, i, list.size());
					table.put(list);
					list.clear(); 
				}*/
			}
			System.out.println("insert data finished");
		} 
		catch( Exception e) {
			e.printStackTrace(System.out);
		}		
	}
	
	public static String genKey(int n) {
		return String.format("%010d", n);
	}
	public static String genData(int n) {
		return String.format("%s", map.get(n%52));
	}

}
