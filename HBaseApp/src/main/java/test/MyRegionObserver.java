package test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;

public class MyRegionObserver extends BaseRegionObserver {
	private static final Log LOG = LogFactory.getLog(MyRegionObserver.class);
	
	@Override
	  public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e,
	      final Get get, final List<Cell> results) throws IOException {
		String row = get.getRow().toString();
		LOG.info(String.format("***************GET hooked:%s", row));
	  }
	@Override
	  public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, 
	      final Put put, final WALEdit edit, final Durability durability) throws IOException {
		String row = put.getRow().toString();
		LOG.info(String.format("***************PUT hooked:%s", row));
	  }
	 @Override
	  public void preDelete(final ObserverContext<RegionCoprocessorEnvironment> e, final Delete delete,
	      final WALEdit edit, final Durability durability) throws IOException {
		 String row = delete.getRow().toString();
			LOG.info(String.format("***************DELETE hooked:%s", row));
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
