package test;

import java.io.IOException;  
  

import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.conf.Configured;  
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;  
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;  
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;  
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;  
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;  
import org.apache.hadoop.mapreduce.Job;  
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapredLoad extends Configured implements Tool{ 
  
    public static class MyMapper extends 
            Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> { 
  
    	private Configuration conf;
    	private HTableInterface tabelInterface;
    	private HConnection connection;
     	
        @Override 
        protected void setup(Context context) throws IOException, 
                InterruptedException { 
            super.setup(context); 
            /*
            conf = context.getConfiguration();
            table = new HTable(conf, "test1");
            table.setAutoFlush(false);*/
            System.out.println("task java.class.path");
            System.out.println(System.getProperties().getProperty("java.class.path"));
            
            conf = HBaseConfiguration.create();
            connection = HConnectionManager.createConnection(conf);
			tabelInterface = connection.getTable("test1");
        } 
        
        @Override
        protected void cleanup(Context context) throws IOException,
        InterruptedException {
	        super.cleanup(context);
	        tabelInterface.flushCommits();
	        tabelInterface.close();
	        connection.close();
        }
  
        @Override 
        protected void map(LongWritable key, Text value, Context context) 
                throws IOException, InterruptedException { 
            String line = value.toString(); 
            String[] terms = line.split("\t"); 
            if ( terms.length == 5 ) { 
                byte[] rowkey = terms[0].getBytes(); 
                Put put = new Put(rowkey);
                put.add("f1".getBytes(), "a".getBytes(), Bytes.toBytes(terms[1]));
                put.add("f1".getBytes(), "b".getBytes(), Bytes.toBytes(terms[2]));
                put.add("f1".getBytes(), "c".getBytes(), Bytes.toBytes(terms[3]));
                put.add("f1".getBytes(), "d".getBytes(), Bytes.toBytes(terms[4]));
                tabelInterface.put(put);
              
            } 
        } 
    } 
  
    public int run(String[] args) { 
    	
    	System.out.println("args:");
    	for(String s:args)
    		System.out.println(s);
  
        if ( args.length < 1 ) { 
            System.err.println("Usage: MapredLoad <data_input_path>"); 
            System.exit(2); 
        } 
        
        try {
        String inputPath = args[0]; 
  
        System.out.println("job java.class.path");
        System.out.println(System.getProperties().getProperty("java.class.path"));
        
        
        Configuration conf = HBaseConfiguration.create(); 
        
        Job job = new Job(conf, "MapredLoad");
          
        job.setMapperClass(MyMapper.class);
        job.setNumReduceTasks(0);
        job.setJarByClass(MapredLoad.class); 
        job.setInputFormatClass(TextInputFormat.class); 
        TextInputFormat.setInputPaths(job, inputPath);
        job.setOutputFormatClass(NullOutputFormat.class);

  
        TableMapReduceUtil.addDependencyJars(job);  // Add HBase jars to tasks' classpath
        
        //HFileOutputFormat.configureIncrementalLoad(job, table); 
  
        //FileInputFormat.addInputPath(job, new Path(inputPath)); 
        //FileOutputFormat.setOutputPath(job, new Path(outputPath)); 
        
        
        System.exit(job.waitForCompletion(true) ? 0 : 1); 
        }catch(Exception e) {
        	e.printStackTrace(System.out);
        	return -1;
        }
        return 0;
    }
    
    public static void main(String[] args) throws IOException {
    	Configuration conf = new Configuration();
    	//String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    	int res = 1;
    	try {
    		res = ToolRunner.run(conf, new MapredLoad (), args);
    	} catch (Exception e) {
    	e.printStackTrace();
    	}
    	System.exit(res);
    	}
    
    } 