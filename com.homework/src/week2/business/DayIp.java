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

public class DayIp {
     public static class IpMapper extends MapReduceBase implements Mapper<Object,Text,Text,IntWritable>{
        private final static  IntWritable one=new IntWritable(1);
		Text ip=new Text();
		@Override
		public void map(Object key, Text value,OutputCollector<Text, IntWritable> output, Reporter reporter)throws IOException {
			// TODO Auto-generated method stub
			Kpi kpi=new Kpi();
			kpi=Kpi.filterIPs(value.toString());
			if(kpi.isValid()==true){
			ip.set(kpi.getRemote_addr());
			output.collect(ip, one);
			}
		}
}
	public static class IpReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{
        private  IntWritable sumresult=new IntWritable(0);
		//private final static IntWritable one =new IntWritable(1);
		private  int sum=0;
		
		@Override
		public void reduce(Text key, Iterator<IntWritable> values,OutputCollector<Text, IntWritable> output, Reporter reporter)throws IOException {
			// TODO Auto-generated method stub
			sum=sum+1;
			
			sumresult.set(sum);
			System.out.print(key+"is:"+sumresult);
			output.collect(key, sumresult);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
         String inpath="hdfs://10.6.3.200:9000/user/hdfs/in/";
         String outpath="hdfs://10.6.3.200:9000/user/hdfs/ip_out/";
         
         JobConf conf=new JobConf(DayIp.class);
         conf.setJobName("depend ip count is:");
         
         conf.setMapOutputKeyClass(Text.class);
         conf.setMapOutputValueClass(IntWritable.class);
         
         conf.setOutputKeyClass(Text.class);
         conf.setOutputValueClass(IntWritable.class);
         
         conf.setMapperClass(IpMapper.class);
         conf.setReducerClass(IpReducer.class);
         conf.setCombinerClass(IpReducer.class);
         
         conf.setInputFormat(TextInputFormat.class);
         conf.setOutputFormat(TextOutputFormat.class);
         
         FileInputFormat.setInputPaths(conf, new Path(inpath));
         FileOutputFormat.setOutputPath(conf,new Path(outpath));
         
         JobClient.runJob(conf);
         System.out.println("finish");
         System.exit(0);
         
	}

}
