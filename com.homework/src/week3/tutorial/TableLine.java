/**  
 * 本包为 Dataguru.cn Hadoop 实战案例课程程序
 * 编写者：James. 
 */  
package tutorial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.io.Text;

/**
 * 定义异常类
 */
class LineException extends Exception
{
	private static final long serialVersionUID = 8245008693589452584L;
	int flag;
	public LineException(String msg, int flag)
	{
		super(msg);
		this.flag = flag;
	}
	public int getFlag()
	{
		return flag;
	}
}


/**  
 * 读取一行数据
 * 提取所要字段
 */  
public class TableLine 
{
	private String imsi, position, time, timeFlag;
	private Date day;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**  
	 * 初始化并检查该行的合法性
	 */  
	public void set ( String line, boolean source, String date, String [] timepoint ) throws LineException
	{
		String [] lineSplit = line.split("\t");
		if( source )
		{
			this.imsi = lineSplit[0];
			this.position = lineSplit[3];
			this.time = lineSplit[4];
		}
		else
		{
			this.imsi = lineSplit[0];
			this.position = lineSplit[2];
			this.time = lineSplit[3];
		}
		
		//检查日期合法性
		if ( ! this.time.startsWith(date) )			//年月日必须与date一致
			throw new LineException("", -1);
		
		try
		{
			this.day = this.formatter.parse(this.time);
		}
		catch ( ParseException e )
		{
			throw new LineException("", 0);
		}
		
		//计算所属时间段
		int i = 0, n = timepoint.length;
		int hour = Integer.valueOf( this.time.split(" ")[1].split(":")[0] );
		while ( i < n && Integer.valueOf( timepoint[i] ) <= hour )
			i++;
		if ( i < n )
		{
			if ( i == 0 )
				this.timeFlag = ( "00-" + timepoint[i] );
			else
				this.timeFlag = ( timepoint[i-1] + "-" + timepoint[i] );
		}
		else 									//Hour大于最大的时间点
			throw new LineException("", -1);
	}
	
	/**  
	 * 输出KEY
	 */  
	public Text outKey()
	{
		return new Text ( this.imsi + "|" + this.timeFlag );
	}
	
	/**  
	 * 输出VALUE
	 */  
	public Text outValue()
	{
		long t = ( day.getTime() / 1000L );						//用时间的偏移量作为输出时间
		return new Text ( this.position + "|" + String.valueOf(t) );
	}
}