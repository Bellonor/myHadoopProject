package recommend;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import  com.homework.hdfs.HdfsDAO;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/*得出物品同现矩阵*/
public class Step2 {

	public static class Step2Mapper extends MapReduceBase implements Mapper<Object,Text,Text,IntWritable>{

		private final static IntWritable v = new IntWritable(1);
		@Override
		public void map(Object key, Text value,OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String[] tokens=MainPodium.DELIMITER.split(value.toString());
			Text k=new Text();
			
			for(int i=1;i<tokens.length;i++){
				String item1=tokens[i].split(":")[0];
				for(int j=1;j<tokens.length;j++){
					String item2=tokens[j].split(":")[0];
					k.set(item1+","+item2);
					output.collect(k, v);
				}
			}
			
		}
		
	}
	public static class Step2Reduce extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{
		 
		@Override
		public void reduce(Text key, Iterator<IntWritable> values,OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
				Integer sum=0;
				while(values.hasNext()){
					sum=sum+values.next().get();
				}
				IntWritable result = new IntWritable();
				//result.set(key+","+sum.toString());
				result.set(sum);
				output.collect(key,result);
		}
		
	}
    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = MainPodium.config();

        String input = path.get("Step2In"); 
        String output = path.get("Step2Out");

        HdfsDAO hdfs = new HdfsDAO(MainPodium.HDFS, conf);
        hdfs.rmr(output);
        
        //conf.setMapOutputKeyClass(Text.class);
        //conf.setMapOutputValueClass(IntWritable.class);
        
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Step2Mapper.class);
        conf.setCombinerClass(Step2Reduce.class);
        conf.setReducerClass(Step2Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }
}
