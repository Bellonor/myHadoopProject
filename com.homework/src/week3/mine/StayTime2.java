package mine;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper.Context;


public class StayTime2 {

	private static class StationMapper extends MapReduceBase implements Mapper<Object,Text,Text,Text>{

/*		int type=1;
		String[] timepoint=new String[]{"09","17","24"};
		String date="2014-03-19";*/
		@Override
		public void map(Object key, Text value,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
				
			int type;
			String[] timepoint=new String[]{"09","17","24"};
			String date="2014-03-19";
		
			//InputSplit inputSplit=reporter.getInputSplit();
			
			FileSplit fs=(FileSplit)reporter.getInputSplit();
			String fileName = fs.getPath().getName();
			if( fileName.endsWith(".pos") )
				type = 0;
			else if ( fileName.endsWith(".net") )
				type = 1;
			else
				throw new IOException("File Name should starts with POS or NET");
			
			StationInfo info=new StationInfo();
			Outinfo outinfo=new Outinfo();
			try {
				outinfo= info.output(value.toString(), type, date, timepoint);
				if(outinfo.isOutValidate())
				output.collect(outinfo.getOutkey(), outinfo.getOutvalue());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		
	}
	
	private static class StationReducer extends MapReduceBase implements Reducer<Text,Text,Text,Text>{

		private String date="2014-03-19";
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		@Override
		public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
			String imsi = key.toString().split("\\|")[0];
			String timeFlag = key.toString().split("\\|")[1];
			TreeMap<Long,String> tr=new TreeMap<Long,String>();    
			while(values.hasNext()){
			    	try {
						String strvalue= values.next().toString();
						tr.put(Long.valueOf(strvalue.split("\\|")[1]), strvalue.split("\\|")[0]);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
			 }
			//在最后添加“OFF”位置
			try {
				Date tmp = formatter.parse( this.date + " " + timeFlag.split("-")[1] + ":00:00" );
				tr.put ( ( tmp.getTime() / 1000L ), "OFF");	
				HashMap<String,Long> maploc=getStayTime(tr);
				Iterator<Entry<String,Long>> iter=maploc.entrySet().iterator();
				while(iter.hasNext()){
					
					Entry<String,Long> entry=iter.next();
					String s1=entry.getKey();
					String v1= entry.getValue().toString();
					Text t1=new Text(key+"|"+s1+"   --"+v1);
					Text t2=new Text(" ");
					output.collect(t2, t1);
					
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}	
				
			
			
		}
		
		public HashMap<String,Long> getStayTime(TreeMap<Long,String> tr){
			HashMap<String,Long> loc=new HashMap<String,Long>();
			Iterator<Entry<Long,String>> it=tr.entrySet().iterator();
			Entry<Long,String> row,nextrow;
			row=it.next();
			
			while(it.hasNext()){
				String key=row.getValue();
				nextrow=it.next();
				Long lon1=row.getKey();
				Long lon2=nextrow.getKey();
				Long lon=lon2-lon1;
				if(lon<3600L){
					if(loc.containsKey(key)){
						Long sum=loc.get(key)+lon;
						loc.put(key,sum);
					}else{
					  loc.put(key, lon);
					}
				}
				row=nextrow;
			}
		  return loc;
		}
	}
	public static void main(String[] args) throws IOException {
        //String inpath="hdfs://localhost:9000/user/hdfs/in/";
        //String outpath="hdfs://localhost:9000/user/hdfs/week3out/";
        JobConf conf=new JobConf(StayTime2.class);
		conf.set("date", args[2]);
		conf.set("timepoint", args[3]);
        conf.setJobName("StatPV");
        conf.setMapperClass(StationMapper.class);
        //conf.setCombinerClass(PvReducer.class);
        conf.setReducerClass(StationReducer.class);
       
        
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);
        
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        JobClient.runJob(conf);
        System.out.println("finish");
        System.exit(0);
	}
}
