package matrix;

import java.io.IOException;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.StringTokenizer;  
  
import org.apache.hadoop.fs.FileSystem;  
import org.apache.hadoop.io.LongWritable;  
import org.apache.hadoop.io.Text;  
import org.apache.hadoop.io.Writable;  
import org.apache.hadoop.io.WritableComparable;  
import org.apache.hadoop.mapred.FileSplit;  
import org.apache.hadoop.mapred.JobConf;  
import org.apache.hadoop.mapred.MapReduceBase;  
import org.apache.hadoop.mapred.Mapper;  
import org.apache.hadoop.mapred.OutputCollector;  
import org.apache.hadoop.mapred.RecordWriter;  
import org.apache.hadoop.mapred.Reducer;  
import org.apache.hadoop.mapred.Reporter;  
import org.apache.hadoop.mapred.TextOutputFormat;  
import org.apache.hadoop.mapred.lib.MultipleOutputFormat;  
import org.apache.hadoop.util.Progressable;  
  
public class Bigmmult {  
     public static final String CONTROL_I = "\u0009";  
     public static final int MATRIX_I = 4;  
     public static final int MATRIX_J = 3;  
     public static final int MATRIX_K = 2;  
      
     public static String makeKey(String[] tokens, String separator) {  
          StringBuffer sb = new StringBuffer();  
          boolean isFirst = true;  
          for (String token : tokens) {  
               if (isFirst)  
                    isFirst = false;  
               else  
                    sb.append(separator);  
               sb.append(token);  
          }  
          return sb.toString();  
     }  
      
     public static class MapClass extends MapReduceBase implements  
               Mapper<LongWritable, Text, Text, Text> {           
          public static HashMap<String , Double> features = new HashMap<String, Double>();  
           
          public void configure(JobConf job) {  
               super.configure(job);  
          }  
           
          public void map(LongWritable key, Text value, OutputCollector<Text, Text> output,  
                    Reporter reporter) throws IOException, ClassCastException {  
               // 获取输入文件的全路径和名称  
               String pathName = ((FileSplit)reporter.getInputSplit()).getPath().toString();  
                
               if (pathName.contains("m_ys_lab_bigmmult_a")) {           
                    String line = value.toString();  
                     
                    if (line == null || line.equals("")) return;  
                    String[] values = line.split(CONTROL_I);  
                     
                    if (values.length < 3) return;  
                     
                    String rowindex = values[0];  
                    String colindex = values[1];  
                    String elevalue = values[2];  
                     
                    for (int i = 1; i <= MATRIX_K; i ++) {  
                         output.collect(new Text(rowindex + CONTROL_I + i), new Text("a#"+colindex+"#"+elevalue));  
                    }  
               }  
                
               if (pathName.contains("m_ys_lab_bigmmult_b")) {                
                    String line = value.toString();  
                    if (line == null || line.equals("")) return;  
                    String[] values = line.split(CONTROL_I);  
                     
                    if (values.length < 3) return;  
                     
                    String rowindex = values[0];  
                    String colindex = values[1];  
                    String elevalue = values[2];  
                     
                    for (int i = 1; i <= MATRIX_I; i ++) {  
                         output.collect(new Text(i + CONTROL_I + colindex), new Text("b#"+rowindex+"#"+elevalue));  
                    }  
               }  
          }  
     }  
  
     public static class Reduce extends MapReduceBase  
               implements Reducer<Text, Text, Text, Text> {  
          public void reduce(Text key, Iterator<Text> values,  
                    OutputCollector<Text, Text> output, Reporter reporter)  
                    throws IOException {  
                
               int[] valA = new int[MATRIX_J];  
               int[] valB = new int[MATRIX_J];  
                
               int i;  
               for (i = 0; i < MATRIX_J; i ++) {  
                    valA[i] = 0;  
                    valB[i] = 0;  
               }  
                
               while (values.hasNext()) {  
                    String value = values.next().toString();  
                    if (value.startsWith("a#")) {  
                         StringTokenizer token = new StringTokenizer(value, "#");  
                         String[] temp = new String[3];  
                         int k = 0;  
                         while(token.hasMoreTokens()) {  
                              temp[k] = token.nextToken();  
                              k++;  
                         }  
                          
                         valA[Integer.parseInt(temp[1])-1] = Integer.parseInt(temp[2]);  
                    } else if (value.startsWith("b#")) {  
                         StringTokenizer token = new StringTokenizer(value, "#");  
                         String[] temp = new String[3];  
                         int k = 0;  
                         while(token.hasMoreTokens()) {  
                              temp[k] = token.nextToken();  
                              k++;  
                         }  
                          
                         valB[Integer.parseInt(temp[1])-1] = Integer.parseInt(temp[2]);  
                    }  
               }  
                
               int result = 0;  
               for (i = 0; i < MATRIX_J; i ++) {  
                    result += valA[i] * valB[i];  
               }  
                
               output.collect(key, new Text(Integer.toString(result)));  
          }  
     }  
} 
