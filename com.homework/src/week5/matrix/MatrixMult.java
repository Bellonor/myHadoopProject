package matrix;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import com.homework.hdfs.HdfsDAO;


public class MatrixMult {

    public static class MyTestMapper extends Mapper<LongWritable, Text, Text, Text> {

        private String flag;// A同现矩阵 or B评分矩阵

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) context.getInputSplit();
            flag = split.getPath().getName();// 判断读的数据集

            // System.out.println(flag);
        }
        private static final int  rowNum = 2;// 矩阵A的行数
        public static final int colA=2;    //矩阵A的列数，B的行数
        private static final int colNum = 2;// 矩阵B的列数
        private  int rowIndexA = 1; // 矩阵A，当前在第几行
        private int rowIndexB = 1; // 矩阵B，当前在第几行
        @Override
        public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
            
            String str=values.toString();
            String[] line=Recommend.DELIMITER.split(str);
            if(line.length==0)return;
            if(flag.equals("Ma")){
                if(line.length!=3)return;
            	String row=line[0];
                String col=line[1];
                String val=line[2];
                Text k=new Text();
                Text v=new Text();
                for(int i=1;i<=colNum;i++){
                	k.set(row+","+i);
                	v.set("a,"+col+","+val);
                	System.out.println(k.toString()+"	"+v.toString());
                	context.write(k, v);
                }
            }
            if(flag.equals("Mb")){
            	String row=line[0];
                String col=line[1];
                String val=line[2];
                Text k=new Text();
                Text v=new Text();
                for(int i=1;i<=rowNum;i++){
                	k.set(i+","+col);
                	v.set("b,"+row+","+val);
                	context.write(k, v);
                	System.out.println(k.toString()+"	"+v.toString());
                }
            }

        }
	
 
    }
    public static class MyTestReducer extends Reducer<Text, Text, Text, Text> {
    	@Override
        public void reduce(Text key,Iterable<Text> values,  Context context) throws IOException, InterruptedException {
    		Map<String,Double> map=new HashMap<String ,Double>();
    		for(Text line:values){
    			String[] arr=Recommend.DELIMITER.split(line.toString());
    			map.put(arr[0]+arr[1], Double.valueOf(arr[2]));
    		}
    		Double sum=0.0;
    		for(int i=1;i<=MyTestMapper.colA;i++){
    			sum=sum+map.get("a"+i)*map.get("b"+i);
    		}
    		Text v=new Text();
    		v.set(sum.toString());
    		context.write(key, v);
    		
    	}
    }
    public static void run(Map<String, String> path) throws IOException, InterruptedException, ClassNotFoundException {
        JobConf conf = Recommend.config();

        String input1 = path.get("matrixMult");
       
        String output = path.get("matrixMultOut");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
        
        hdfs.rmr(output);
        hdfs.rmr(input1);
        hdfs.mkdirs(input1);
        hdfs.copyFile("datafile/week5/Ma", input1);
        hdfs.copyFile("datafile/week5/Mb", input1);
        Job job = new Job(conf);
        job.setJarByClass(MatrixMult.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(MyTestMapper.class);
        job.setReducerClass(MyTestReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(input1));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
}
