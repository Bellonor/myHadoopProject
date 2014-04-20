package filterSalary;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.homework.hdfs.HdfsDAO;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
//第二步， MapReduce后产生:  用户，浏览过职位薪水相加*0.8
/*1.map
	key:userid
	value:salary
2.reduce
   key:userid
   value:平均薪水*0.8*/
public class Step2 {

	public static class Step2Mapper extends Mapper<LongWritable, Text, Text, Text> {

       @Override
        public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
            
            String str=values.toString();
            String[] line=Main.DELIMITER.split(str);
            if(line.length==0)return;
           String userid=line[0];
           String salary=line[2];
           Text k=new Text();
           Text v=new Text();
           k.set(userid);
           v.set(salary);
           context.write(k, v);
        }
	}
    public static class Step2Reducer extends Reducer<Text, Text, Text, Text> {
    	@Override
        public void reduce(Text key,Iterable<Text> values,  Context context) throws IOException, InterruptedException {
    		Integer i=0;
    		Double sum=0.0;
    		for(Text value:values){
    			i=i+1;
    			Double val=Double.valueOf(value.toString());
    			sum=sum+val;
    		}
    		Double  average=sum/i;
    		Double va=average*0.8;
			
			Text v=new Text();
			v.set(va.toString());
			context.write(key, v);
    	}
    }
    public static void run(Map<String, String> path) throws IOException, ClassNotFoundException, InterruptedException {
        JobConf conf = Main.config();

        String input = path.get("Step2In");
        String output = path.get("Step2Out");

        HdfsDAO hdfs = new HdfsDAO(Main.HDFS, conf);
        hdfs.rmr(output);
        Job job = new Job(conf);
        job.setJarByClass(Step2.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Step2Mapper.class);
        job.setReducerClass(Step2Reducer.class);
        
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
}
