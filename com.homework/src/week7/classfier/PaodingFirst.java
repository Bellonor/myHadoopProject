package classfier;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.homework.hdfs.HdfsDAO;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
public class PaodingFirst {
	public static class PaodingFirstMapper extends Mapper<LongWritable, Text, Text, Text> {

        private String flag;//  
        PaodingAnalyzer analyzer = new PaodingAnalyzer();
        Text v=new Text();
        Text k=new Text();
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) context.getInputSplit();
            flag = split.getPath().getParent().getName();// 判断读的数据集

            // System.out.println(flag);
        }
        
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            
            k.set(flag);
            PaodingAnalyzer analyzer=new PaodingAnalyzer();
            StringReader sr=new StringReader(value.toString());
            TokenStream ts=analyzer.tokenStream("", sr);
            StringBuilder sb=new StringBuilder();
            try{
            	while(ts.incrementToken()){
            		CharTermAttribute ta=ts.getAttribute(CharTermAttribute.class);
            		sb.append(ta.toString());
            		sb.append(" ");
            		System.out.print(ta.toString()+" ");
            	}
            }catch(Exception e){
            	
            }
            System.out.println();
            v.set(sb.toString());
            context.write(k, v);
        }
	}
    
    	
    public static void run(Map<String, String> path) throws IOException, ClassNotFoundException, InterruptedException {
        JobConf conf = Main.config();

        String input = path.get("PaodingFirstIn");
        String output = path.get("PaodingFirstOut");

        HdfsDAO hdfs = new HdfsDAO(Main.HDFS, conf);
        hdfs.rmr(output);
        //hdfs.copyFile(path.get("ToHdfsData1"), input);
        Job job = new Job(conf);
        job.setJarByClass(PaodingFirst.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(PaodingFirstMapper.class);
       

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

       Path inpath= new Path(input);
		try {                                            //  input path
			FileSystem fs = inpath.getFileSystem(conf);
			FileStatus[] stats = fs.listStatus(inpath);
			for(int i=0; i<stats.length; i++)
				FileInputFormat.addInputPath(job, stats[i].getPath());
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
        
        //FileInputFormat.setInputPaths(job, new Path(input));
        FileOutputFormat.setOutputPath(job, new Path(output));

        job.waitForCompletion(true);
    }
	
	
}
