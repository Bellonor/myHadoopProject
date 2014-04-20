package tutorial;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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

//位置数据
//IMSI|IMEI|UPDATETYPE|CGI|TIME
//上网数据
//IMSI|IMEI|CGI|TIME|CGI|URL

/**  
 * 汇总基站数据表
 * 计算每个用户在不同的时间段不同的基站停留的时长
 * 输入参数 < input path > < output path > < date > < timepoint >
 * 参数示例： “/base /output 2012-09-12 09-17-24"
 * 意味着以“/base”为输入，"/output"为输出，指定计算2012年09月12日的数据，并分为00-07，07-17，17-24三个时段
 * 输出格式 “IMSI|CGI|TIMFLAG|STAY_TIME”
 */  
public class BaseStationDataPreprocess extends Configured implements Tool 
{
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

	/**  
	 * 读取一行数据
	 * 以“IMSI+时间段”作为 KEY 发射出去
	 */  
	public static class Map extends Mapper<LongWritable, Text, Text, Text> 
	{
		String date;
		String [] timepoint;
		boolean dataSource;

		/** 
		 * 初始化
		 */ 
		public void setup ( Context context ) throws IOException
		{
			this.date = context.getConfiguration().get("date");							//读取日期
			this.timepoint = context.getConfiguration().get("timepoint").split("-");	//读取时间分割点
			
			//提取文件名
			FileSplit fs = (FileSplit)context.getInputSplit();
			String fileName = fs.getPath().getName();
			if( fileName.startsWith("POS") )
				dataSource = true;
			else if ( fileName.startsWith("NET") )
				dataSource = false;
			else
				throw new IOException("File Name should starts with POS or NET");
		}

		/**  
		 * MAP任务
		 * 读取基站数据
		 * 找出数据所对应时间段
		 * 以IMSI和时间段作为 KEY
		 * CGI和时间作为 VALUE
		 */ 
		public void map ( LongWritable key, Text value, Context context ) throws IOException, InterruptedException 
		{
			String line = value.toString();
			TableLine tableLine = new TableLine();

			//读取行
			try
			{
				tableLine.set(line, this.dataSource, this.date, this.timepoint );
			}
			catch ( LineException e )
			{
				if(e.getFlag()==-1)
					context.getCounter(Counter.OUTOFTIMESKIP).increment(1);
				else
					context.getCounter(Counter.TIMESKIP).increment(1);
				return;
			}
			catch (Exception e)
			{
				context.getCounter(Counter.LINESKIP).increment(1);
				return;
			}

			context.write( tableLine.outKey(), tableLine.outValue() );
		}
	}

	/**  
	 * 统计同一个IMSI在同一时间段
	 * 在不同CGI停留的时长
	 */ 
	public static class Reduce extends Reducer<Text, Text, NullWritable, Text> 
	{
		private String date;
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		/**  
		 * 初始化
		 */ 
		public void setup ( Context context )
		{
			this.date = context.getConfiguration().get("date");							//读取日期
		}

		public void reduce ( Text key, Iterable<Text> values, Context context ) throws IOException, InterruptedException
		{
			String imsi = key.toString().split("\\|")[0];
			String timeFlag = key.toString().split("\\|")[1];

			//用一个TreeMap记录时间
			TreeMap<Long, String> uploads = new TreeMap<Long, String>();
			String valueString;

			for ( Text value : values )
			{
				valueString = value.toString();
				try 
				{
					uploads.put( Long.valueOf( valueString.split("\\|")[1] ), valueString.split("\\|")[0] );
				}
				catch ( NumberFormatException e ) 
				{
					context.getCounter(Counter.TIMESKIP).increment(1);
					continue;
				}
			}
			
			try 
			{
				//在最后添加“OFF”位置
				Date tmp = this.formatter.parse( this.date + " " + timeFlag.split("-")[1] + ":00:00" );
				uploads.put ( ( tmp.getTime() / 1000L ), "OFF");		
				
				//汇总数据
				HashMap<String, Float> locs = getStayTime(uploads);

				//输出
				for( Entry<String, Float> entry : locs.entrySet() )
				{
					StringBuilder builder = new StringBuilder();
					builder.append(imsi).append("|");
					builder.append(entry.getKey()).append("|");
					builder.append(timeFlag).append("|");
					builder.append(entry.getValue());
					
					context.write( NullWritable.get(), new Text(builder.toString()) );
				}
			}
			catch ( Exception e )
			{
				context.getCounter(Counter.USERSKIP).increment(1);
				return;
			}
		}
	
		/**
		 * 获得位置停留信息,KEY＝用户｜时间段，VALUE＝基站｜时间
		 * 对一个KEY，对不同的基站进行计算停留时间
		 */
		private HashMap<String, Float> getStayTime(TreeMap<Long, String> uploads)
		{
			Entry<Long, String> upload, nextUpload;
			HashMap<String, Float> locs = new HashMap<String, Float>();
			//初始化
			Iterator<Entry<Long, String>> it = uploads.entrySet().iterator();
			upload = it.next();
			//计算
			while( it.hasNext() )
			{
				nextUpload = it.next();
				float diff = (float) (nextUpload.getKey()-upload.getKey()) / 60.0f;
				//停留时间的计算方法是：本基站最前的时间＋下一个基站的最前的时间，中间有超过60分钟的视为关机
				if( diff <= 60.0 )									//时间间隔过大则代表关机
				{
					if( locs.containsKey( upload.getValue() ) )
						locs.put( upload.getValue(), locs.get(upload.getValue())+diff ); 
					else
						locs.put( upload.getValue(), diff );
				}
				upload = nextUpload;
			}
			return locs;
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		conf.set("date", args[2]);
		conf.set("timepoint", args[3]);

		Job job = new Job(conf, "BaseStationDataPreprocess");
		job.setJarByClass(BaseStationDataPreprocess.class);

		FileInputFormat.addInputPath( job, new Path(args[0]) );			//输入路径
		FileOutputFormat.setOutputPath( job, new Path(args[1]) );		//输出路径

		job.setMapperClass( Map.class );								//调用上面Map类作为Map任务代码
		job.setReducerClass ( Reduce.class );							//调用上面Reduce类作为Reduce任务代码
		job.setOutputFormatClass( TextOutputFormat.class );
		job.setOutputKeyClass( Text.class );
		job.setOutputValueClass( Text.class );

		job.waitForCompletion(true);

		return job.isSuccessful() ? 0 : 1;
	}

	public static void main(String[] args) throws Exception 
	{
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
		int res = ToolRunner.run(new Configuration(), new BaseStationDataPreprocess(), args);

		System.exit(res);
	}
}