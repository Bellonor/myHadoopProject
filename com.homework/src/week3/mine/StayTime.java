package mine;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/*//输入参数
///hdfs://localhost:9000/user/hdfs/in/  hdfs://localhost:9000/user/hdfs/week3out 2014-03-19 09-17-24
*/
public class StayTime extends Configured implements Tool {

	/**  
	 * 计数器
	 * 用于计数各种异常数据
	 */  
	enum Counter 
	{
		TIMESKIP,		//时间格式有误
		OUTOFTIMESKIP,	//时间不在参数指定的时间段内
		LINESKIP,		//源文件行有误
		USERSKIP		//某个用户某个时间段被整个放弃
	}
	
	private static class StayMapper extends Mapper<Object,Text,Text,Text>{

		int type;
		String[] timepoint;
		String date;
		
		public void setup(Context context)throws IOException{
			this.date = context.getConfiguration().get("date");							//读取日期
			this.timepoint = context.getConfiguration().get("timepoint").split("-");	//读取时间分割点
			FileSplit fs=(FileSplit)context.getInputSplit();
			String fileName = fs.getPath().getName();
			if( fileName.endsWith(".pos") )
				type = 0;
			else if ( fileName.endsWith(".net") )
				type = 1;
			else
				throw new IOException("File Name should starts with POS or NET");
		}
		
		public void map(Object key, Text value,Context context)throws IOException , InterruptedException {
		     
			StationInfo info=new StationInfo();
			Outinfo outinfo=new Outinfo();
			try {
				outinfo= info.output(value.toString(), type, date, timepoint);
				context.write(outinfo.getOutkey(), outinfo.getOutvalue());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
	}
    private static class StayReducer extends Reducer<Text,Text,Text,Text>{

		private String date;
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		/**  
		 * 初始化
		 */ 
		public void setup ( Context context )
		{
			this.date = context.getConfiguration().get("date");							//读取日期
		}
		
		
		public void reduce(Text key, Iterator<Text> values, Context context)throws IOException , InterruptedException{
				
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
					Text t1=new Text(key+"|"+s1+"--"+v1);
					context.write(key, t1);
					
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
			String key=row.getValue();
			while(it.hasNext()){
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
	
   
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		conf.set("date", args[2]);
		conf.set("timepoint", args[3]);

		Job job = new Job(conf, "staytime");
		job.setJarByClass(StayTime.class);

		FileInputFormat.addInputPath( job, new Path(args[0]) );			//输入路径
		FileOutputFormat.setOutputPath( job, new Path(args[1]) );		//输出路径

		job.setMapperClass( StayMapper.class );	 //调用上面Map类作为Map任务代码
		job.setCombinerClass(StayReducer.class);
		job.setReducerClass ( StayReducer.class );							//调用上面Reduce类作为Reduce任务代码
		job.setOutputFormatClass( TextOutputFormat.class );
		
		job.setOutputKeyClass( Text.class );
		job.setOutputValueClass( Text.class );

		job.waitForCompletion(true);

		return job.isSuccessful() ? 0 : 1;
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if ( args.length != 4 )
		{
			System.err.println("");
			System.err.println("Usage: BaseStationDataPreprocess < input path > < output path > < date > < timepoint >");
			System.err.println("Example: BaseStationDataPreprocess /user/james/Base /user/james/Output 2012-09-12 07-09-17-24");
			System.err.println("Warning: Timepoints should be begined with a 0+ two digit number and the last timepoint should be 24");
			System.err.println("Counter:");
			System.err.println("\t"+"TIMESKIP"+"\t"+"Lines which contain wrong date format");
			System.err.println("\t"+"OUTOFTIMESKIP"+"\t"+"Lines which contain times that out of range");
			System.err.println("\t"+"LINESKIP"+"\t"+"Lines which are invalid");		
			System.err.println("\t"+"USERSKIP"+"\t"+"Users in some time are invalid");		
			System.exit(-1);
		}

		//运行任务
		int res = ToolRunner.run(new Configuration(), new StayTime(), args);
        //System.out.println("finish");
		System.exit(res);
	}

}
