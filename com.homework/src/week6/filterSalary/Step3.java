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
public class Step3 {
	public static class Step3Mapper extends Mapper<LongWritable, Text, Text, Text> {

        private String flag;//  

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) context.getInputSplit();
            flag = split.getPath().getParent().getName();// 判断读的数据集

            // System.out.println(flag);
        }
        
        @Override
        public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
            
            String str=values.toString();
            String[] line=Main.DELIMITER.split(str);
            if(line.length==0)return;
            if(flag.equals("step0Out")){
            	String userid=line[0];
                String jobid=line[1];
                String salary=line[2];
                Text k=new Text();
                Text v=new Text();
                k.set(userid);
                v.set(jobid+","+salary);
                context.write(k, v);
            }
            if(flag.equals("step2Out")){
                if(line.length!=2)return;
            	String userid=line[0];
                String salary=line[1];
                Text k=new Text();
                Text v=new Text();
                k.set(userid);
                v.set("average,"+salary);
                context.write(k, v);
            }


        }
	}
    public static class Step3Reducer extends Reducer<Text, Text, Text, Text> {
    	@Override
        public void reduce(Text key,Iterable<Text> values,  Context context) throws IOException, InterruptedException {
    		Map<String,String> map=new HashMap<String,String>();
    		Integer i=0;
    		for(Text value:values){
    			String[] arr=Main.DELIMITER.split(value.toString());
    			i=i+1;
    			if(arr[0].equals("average"))
    			map.put(arr[0], arr[1]);
    			else
    			{
    				map.put(arr[0], arr[1]);
    			}
    		}
    		String salary=map.get("average");
    	    Double average=Double.valueOf(salary);
    	    StringBuilder sb=new StringBuilder();
    		for(Map.Entry<String,String> entry:map.entrySet()){
    			if(entry.getKey().equals("average"))continue;

    			Double val=Double.valueOf(entry.getValue());
    			if(val>=average){
    				sb.append("(推荐职位ID："+entry.getKey()+",薪水："+entry.getValue()+"),");
    			}

    		}
    		String result = String.format("%.2f", average);
    		if(sb.length()>1){
    			sb.append("(%80平均薪水:"+result+")");
    		}
			Text k=new Text();
			Text v=new Text();
    		k.set("用户:"+key);
			v.set(sb.toString());
			context.write(k, v);
    		
    	}
    }
    public static void run(Map<String, String> path) throws IOException, ClassNotFoundException, InterruptedException {
        JobConf conf = Main.config();

        String input1 = path.get("Step3In1");
        String input2 = path.get("Step3In2");
        String output = path.get("Step3Out");

        HdfsDAO hdfs = new HdfsDAO(Main.HDFS, conf);
//        hdfs.rmr(output);
        hdfs.rmr(output);

        
        Job job = new Job(conf);
        job.setJarByClass(Step3.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Step3Mapper.class);
        job.setReducerClass(Step3Reducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input1),new Path(input2));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
}
