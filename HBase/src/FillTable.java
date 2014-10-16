import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;

//java -Djava.ext.dirs=/opt/ibm/java7/jre/lib/ext:/root/hbase/lib FillTable test4 100

public class FillTable {
	
	private static Configuration conf;
	
	static {
		conf = HBaseConfiguration.create();
	}

	public static void main(String[] args) {
		String tableName = "test";
		if(args.length > 0)
			tableName = args[0];
		int num = 100;
		if(args.length > 1)
			num = Integer.parseInt(args[1]);
		long start = System.currentTimeMillis();
		createTable(tableName);	
		insertData(tableName, num);
		long end = System.currentTimeMillis();
		System.out.printf("total: %d seconds\n", (end - start)/1000);
	}
	
	public static void createTable(String tableName) {
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
			admin.createTable(tableDesc);
			System.out.printf("table %s created\n", tableName);
			admin.close();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static void insertData(String tableName, int numOfRows) {
		System.out.println("start insert data");
		//HTablePool pool = new HTablePool(conf, 100);
		//HConnection connection = HConnectionManager.createConnection(config);
		//HTableInterface table = connection.getTable(TableName.valueOf("table1"));
		
		try( HConnection connection = HConnectionManager.createConnection(conf);
			HTableInterface table = connection.getTable(tableName)) {
		    // Use the table as needed, for a single operation and a single thread
			List<Put> list = new ArrayList<Put>(10000);
			
			for(int i = 0; i < numOfRows; i++) {
				Put put = new Put(String.format("ROW%010d", i).getBytes());
				put.add("f1".getBytes(), "a".getBytes(), String.format("VALUE%010d", i).getBytes());
				list.add(put);
				
				if(i+1 % 10000 == 0 || i == numOfRows -1) {
					table.put(list);
					list.clear();
				}
			}
			System.out.println("insert data finished");
		} catch( Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

}
