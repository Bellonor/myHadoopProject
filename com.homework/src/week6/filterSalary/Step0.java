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
public class Step0 {
	public static class Step0Mapper extends Mapper<LongWritable, Text, Text, Text> {

        private String flag;//  

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) context.getInputSplit();
            flag = split.getPath().getName();// 判断读的数据集

            // System.out.println(flag);
        }
        
        @Override
        public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
            
            String str=values.toString();
            String[] line=Main.DELIMITER.split(str);
            if(line.length==0)return;
            if(flag.equals("mahoutA.txt")){
            	String userid=line[0];
            	for(int i=1;i<line.length;i++){
                    String jobid=line[i];
                    Text k=new Text();
                    Text v=new Text();
                    k.set(jobid);
                    v.set("user,"+userid);
                    context.write(k, v);
            	}
 
            }
            if(flag.equals("job.csv")){
                if(line.length!=3)return;
            	String jobid=line[0];
                String salary=line[2];
                Text k=new Text();
                Text v=new Text();
                k.set(jobid);
                v.set("job,"+salary);
                context.write(k, v);
            }


        }
	}
    public static class Step0Reducer extends Reducer<Text, Text, Text, Text> {
    	@Override
        public void reduce(Text key,Iterable<Text> values,  Context context) throws IOException, InterruptedException {
    		Map<String,String> map=new HashMap<String,String>();
    		Integer i=0;
    		for(Text value:values){
    			String[] arr=Main.DELIMITER.split(value.toString());
    			i=i+1;
    			if(arr[0].equals("job"))
    			map.put(arr[0], arr[1]);
    			else
    			{
    				map.put(i.toString(), arr[1]);
    			}
    		}
    		String salary=map.get("job");
    	    for(Map.Entry<String,String> entry:map.entrySet()){
    			if(entry.getKey().equals("job"))continue;
    			Text k=new Text();
    			Text v=new Text();
    			k.set(entry.getValue());
    			v.set(key.toString()+","+salary);
    			context.write(k, v);
    		}
    	}
    }
    public static void run(Map<String, String> path) throws IOException, ClassNotFoundException, InterruptedException {
        JobConf conf = Main.config();

        String input = path.get("Step0In");
        String output = path.get("Step0Out");

        HdfsDAO hdfs = new HdfsDAO(Main.HDFS, conf);
        hdfs.rmr(output);
        //hdfs.copyFile(path.get("ToHdfsData1"), input);
        Job job = new Job(conf);
        job.setJarByClass(Step0.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Step0Mapper.class);
        job.setReducerClass(Step0Reducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
}
