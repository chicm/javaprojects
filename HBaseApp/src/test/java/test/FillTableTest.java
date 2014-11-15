package test;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FillTableTest {
	  private final static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
	  private final static String TABLE_NAME = "testRowCounter";
	  private final static String COL_FAM = "col_fam";
	  private final static String COL1 = "c1";
	  private final static String COL2 = "c2";
	 @BeforeClass
	  public static void setUpBeforeClass() throws Exception {
		 /*
		System.out.println("before testing");
	    TEST_UTIL.startMiniCluster();
	    TEST_UTIL.startMiniMapReduceCluster(); */
	    //Table table = TEST_UTIL.createTable(TableName.valueOf(TABLE_NAME), Bytes.toBytes(COL_FAM));
	    //writeRows(table);
	    //table.close();
	    
	   // String[] args = {"start"};
	    //HMaster.main(args);
	  }
	 /**
	   * @throws java.lang.Exception
	   */
	  @AfterClass
	  public static void tearDownAfterClass() throws Exception {
		  /*
	    TEST_UTIL.shutdownMiniCluster();
	    TEST_UTIL.shutdownMiniMapReduceCluster();*/
	    
	    //String[] args = {"stop"};
	    //HMaster.main(args);
	  }
	  
	 @Test
	 public void testMain() {
		 /*
		 System.out.println("******************start testing");
		 String[] args = {"test1", "100"};
		 FillTable.main(args);
		 System.out.println("*******************end testing"); */
	 }


}
