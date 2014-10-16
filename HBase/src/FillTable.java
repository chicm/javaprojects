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
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;

//java -Xmx2048m -Djava.ext.dirs=/opt/ibm/java7/jre/lib/ext:/root/hbase/lib FillTable test4 100

public class FillTable implements Runnable{
	
	private static Configuration conf;
	private static HashMap<Integer, String> map;
	private static final int CACHE_NUMBER = 100000;
	private static final int NUM_REGIONS = 4;
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
			char[] c = new char[40];
			Arrays.fill(c, (char)('A' + i));
			map.put(i, new String(c));
		}
		for(int i = 0; i < 26; i++) {
			char[] c = new char[40];
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
		
		FillTable t1 = new FillTable(0, num/2-1);
		FillTable t2 = new FillTable(num/2, num-1);
		ExecutorService es = Executors.newFixedThreadPool(2); 
		es.submit(t1);
		es.submit(t2);
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
			admin.createTable(tableDesc, genData(0).getBytes(), genData(num-1).getBytes(), NUM_REGIONS);
			System.out.printf("table %s created\n", tableName);
			admin.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static void insertData(String tableName, int startKey, int endKey) {
		System.out.println("start insert data");
		
		try( HConnection connection = HConnectionManager.createConnection(conf);
			HTableInterface table = connection.getTable(tableName)) {
			table.setAutoFlush(false);
		    // Use the table as needed, for a single operation and a single thread
			List<Put> list = new ArrayList<Put>(CACHE_NUMBER);
			
			for(int i = startKey; i <= endKey; i++) {
				Put put = new Put(genData(i).getBytes());
				put.setDurability(Durability.SKIP_WAL);
				put.add("f1".getBytes(), "a".getBytes(), genData(i).getBytes());
				list.add(put);
				
				if(i % 100000 == 0) {
					System.out.printf("count:%d\n", i);
				}
				
				if( (i!=startKey && (i-startKey+1) % CACHE_NUMBER == 0) || i == endKey) {
					System.out.printf("***PUT: n=%d, list.size=%d\n", i, list.size());
					table.put(list);
					list.clear();
				}
			}
			System.out.println("insert data finished");
		} catch( Exception e) {
			e.printStackTrace(System.out);
		}
		
	}
	
	public static String genData(int n) {
		return String.format("%010d%s", n, map.get(n%52));
	}

}
