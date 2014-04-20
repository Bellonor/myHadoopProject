package business;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import entity.Kpi;

public class StatPV {

	private static class PvMapper extends MapReduceBase implements Mapper<Object,Text,Text,IntWritable>{

		private IntWritable one=new IntWritable(1);
		private Text pvtxt=new Text();
		@Override
		public void map(Object key, Text value,OutputCollector<Text, IntWritable> output, Reporter reporter)throws IOException {
			// TODO Auto-generated method stub
			try {
				Kpi kpi=Kpi.filterPVs(value.toString());
				pvtxt.set("pv");
				output.collect(pvtxt, one);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private static class PvReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{

		private IntWritable result=new IntWritable(0);
		 
		@Override
		public void reduce(Text key, Iterator<IntWritable> values,OutputCollector<Text, IntWritable> output, Reporter reporter)throws IOException {
			// TODO Auto-generated method stub
			int sum=0;
			try {
				while(values.hasNext()){
					
					sum=sum+ values.next().get();
				}
				result.set(sum);
				output.collect(key, result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) throws IOException {
        String inpath="hdfs://localhost:9000/user/hdfs/in/";
        String outpath="hdfs://localhost:9000/user/hdfs/pv_out/";
        JobConf conf=new JobConf(StatPV.class);
        conf.setJobName("StatPV");
        conf.setMapperClass(PvMapper.class);
        conf.setCombinerClass(PvReducer.class);
        conf.setReducerClass(PvReducer.class);
       
        
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);
        
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(inpath));
        FileOutputFormat.setOutputPath(conf, new Path(outpath));
        JobClient.runJob(conf);
        System.out.println("finish");
        System.exit(0);
	}

}
