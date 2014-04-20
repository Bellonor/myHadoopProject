package recommend;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.homework.hdfs.HdfsDAO;

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
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/*得出以下结果
1       102:3.0,103:2.5,101:5.0
2       101:2.0,102:2.5,103:5.0,104:2.0
3       107:5.0,101:2.0,104:4.0,105:4.5
4       101:5.0,103:3.0,104:4.5,106:4.0
5       101:4.0,102:3.0,103:2.0,104:4.0,105:3.5,106:4.0*/
public class Step1 {

	public static class Step1Map extends MapReduceBase implements Mapper<Object,Text,Text,Text>{

		@Override
		public void map(Object key, Text value,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
			String[] tokens=MainPodium.DELIMITER.split(value.toString());
			Text k=new Text();
			Text v=new Text();
			k.set(tokens[0]);
			v.set(tokens[1]+":"+tokens[2]);
			output.collect(k, v);	
		}
		
	}
	public static class Step1Reduce extends MapReduceBase implements Reducer<Text,Text,Text,Text>{

		@Override
		public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
			Text v=new Text();
			String str="";
			while(values.hasNext()){
				str=str+values.next()+",";
			}
			int n=str.lastIndexOf(",");
			
			v.set(str.substring(0,n));	
			output.collect(key, v);
			
		}
     }
	
    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = MainPodium.config();

        String input = path.get("Step1In");
        String output = path.get("Step1Out");

        HdfsDAO hdfs = new HdfsDAO(MainPodium.HDFS, conf);
//        hdfs.rmr(output);
        hdfs.rmr(output);
        hdfs.rmr(input);
        hdfs.mkdirs(input);
       hdfs.copyFile(path.get("data"), input);

        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step1Map.class);
        conf.setCombinerClass(Step1Reduce.class);
        conf.setReducerClass(Step1Reduce.class);

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
