package recommend;
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
/*SparseMatrix乘法*/
public class Step4 {

    public static class SparseMatrixMapper extends Mapper<LongWritable, Text, Text, Text> {

        private String flag;// A同现矩阵 or B评分矩阵

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) context.getInputSplit();
            flag = split.getPath().getParent().getName();// 判断读的数据集

            // System.out.println(flag);
        }
        private static final int  rowNum = 4;// 矩阵A的行数
        public static final int colA=3;    //矩阵A的列数，B的行数
        private static final int colNum = 2;// 矩阵B的列数

        @Override
        public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
            
            String str=values.toString();
            String[] line=MainPodium.DELIMITER.split(str);
            if(line.length==0)return;
            if(flag.equals("step2Out")){
                if(line.length!=3)return;
            	String row=line[0];
                String col=line[1];
                String val=line[2];
                Text k=new Text();
                Text v=new Text();
                for(int i=1;i<=colNum;i++){
                	k.set(row+",10"+i);
                	v.set("a,"+col+","+val);
                	System.out.println(k.toString()+"	"+v.toString());
                	context.write(k, v);
                }
            }
            if(flag.equals("step3Out")){
            	String row=line[0];
                String col=line[1];
                String val=line[2];
                Text k=new Text();
                Text v=new Text();
                for(int i=1;i<=rowNum;i++){
                	k.set("10"+i+","+col);
                	v.set("b,"+row+","+val);
                	context.write(k, v);
                	System.out.println(k.toString()+"	"+v.toString());
                }
            }

        }
	
 
    }
    public static class SparseMatrixReducer extends Reducer<Text, Text, Text, Text> {
    	@Override
        public void reduce(Text key,Iterable<Text> values,  Context context) throws IOException, InterruptedException {
    		
    		Map<String,Double> mapA=new HashMap<String,Double>();
    		Map<String,Double> mapB=new HashMap<String,Double>();
    		
    		for(Text line:values){
    			String val=line.toString();
    			if(val.contains("a")){
    				String[] arr=MainPodium.DELIMITER.split(val);
    				mapA.put(arr[1], Double.valueOf(arr[2]));
    				
    			}
    			else if(val.contains("b")){
    				String[] arr=MainPodium.DELIMITER.split(val);
    				mapB.put(arr[1], Double.valueOf(arr[2]));
    			}
    		}
    		Double sum=0.0;
    		for(Map.Entry<String, Double> entry:mapA.entrySet()){
    			Double bval=mapB.get(entry.getKey())==null?0.0:mapB.get(entry.getKey());
    			sum=sum+(entry.getValue()*bval);
    		}
    		
    		Text v=new Text();
    		v.set(sum.toString());
    		context.write(key, v);
    		
    	}
    }
    public static void run(Map<String, String> path) throws IOException, InterruptedException, ClassNotFoundException {
        JobConf conf = MainPodium.config();

        String input1 = path.get("Step4In1"); 
        String input2 = path.get("Step4In2"); 
        String output = path.get("Step4Out");
      
        HdfsDAO hdfs = new HdfsDAO(MainPodium.HDFS, conf);
        hdfs.rmr(output);
        Job job = new Job(conf);
        job.setJarByClass(Step4.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(SparseMatrixMapper.class);
        job.setReducerClass(SparseMatrixReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input1),new Path(input2));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
}
