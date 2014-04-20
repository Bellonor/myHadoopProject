package recommend;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.homework.hdfs.HdfsDAO;

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

/*得出用户评分矩阵，用户名从左到右排*/
public class Step3 {
	public static class Step3Mapper extends MapReduceBase implements Mapper<Object,Text,NullWritable,Text>{

		private final static IntWritable v = new IntWritable(1);
		@Override
		public void map(Object key, Text value,OutputCollector<NullWritable, Text> output, Reporter reporter)
				throws IOException {
			String[] tokens=MainPodium.DELIMITER.split(value.toString());
			Text k=new Text();
			for(int i=1;i<tokens.length;i++){
				Text v=new Text();
				String str=tokens[i].split(":")[0]+",10"+tokens[0]+","+tokens[i].split(":")[1];
				v.set(str);
				output.collect(NullWritable.get(),v);
			}
			
			
		}
		
	}
	public static void run(Map<String, String> path) throws IOException {
        JobConf conf = MainPodium.config();

        String input = path.get("Step3In"); 
        String output = path.get("Step3Out");

        HdfsDAO hdfs = new HdfsDAO(MainPodium.HDFS, conf);
        hdfs.rmr(output);

        conf.setOutputKeyClass(NullWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step3Mapper.class);
        //conf.setCombinerClass(Step2Reduce.class);
        //conf.setReducerClass(Step2Reduce.class);

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
