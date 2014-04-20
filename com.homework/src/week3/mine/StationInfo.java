package mine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;



public class StationInfo {

	 private String imsi;
	 private String imei;
	 private String updatetype;
	 private String local;
	 private Date time;
	 private String url;
	 private Integer type;
	 private boolean validate=true;
	 
	 //type=0表示POS位置信息，1表示NET上网记录
	 private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 
	 public StationInfo init(String line,int type) throws ParseException{
		 
		StationInfo info= new StationInfo();
		if(line==null||line.trim().length()<=0){
			info.setValidate(false);
			return info;
		}
		try {
			 
			 if(line.length()>0){
				 String[] arr=line.split("\t");
				 if(arr.length!=5){
					 info.setValidate(false);
					 return info;
				 }
				 if(type==0){
					 info.setImsi(arr[0]);
					 info.setImei(arr[1]);
					 info.setUpdatetype(arr[2]);
					 info.setLocal(arr[3]);
					 info.setTime(this.formatter.parse(arr[4]));
				 }else if(type==1){
					 info.setImsi(arr[0]);
					 info.setImei(arr[1]);
					 info.setLocal(arr[2]);
					 info.setTime(this.formatter.parse(arr[3]));
					 info.setUrl(arr[4]);
				 }
			 }
			 else{
				 info.setValidate(false);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 info.setValidate(false);
			 return info;
		}
		 return info;
		 
	 }
	 //date表示所要计算的日期
	 public Outinfo output(String line,int type,String date,String[] timepoint) throws ParseException{
		 
		 StationInfo info=new StationInfo();
		 Outinfo outinfo=new Outinfo();
		 String timeFlag;
		 
		 try {
			info=info.init(line, type);
			if(!info.isValidate()){
				outinfo.setOutValidate(false);
				return outinfo;
			}
			 String dateValue= formatter.format(info.getTime());
			 if(!dateValue.startsWith(date)){
				 outinfo.setOutValidate(false);
			 }
			 
				//计算所属时间段
				int i = 0, n = timepoint.length;
				int hour = Integer.valueOf( dateValue.split(" ")[1].split(":")[0] );
				while ( i < n && Integer.valueOf( timepoint[i] ) <= hour )
					i++;
				if ( i < n )
				{
					if ( i == 0 )
						timeFlag = ( "00-" + timepoint[i] );
					else
						timeFlag = ( timepoint[i-1] + "-" + timepoint[i] );
				}
				else 									//Hour大于最大的时间点
					timeFlag="unknow";
				String outkey=info.getImsi()+"|"+timeFlag;
			    Text keytext=new Text();
			    Text valuenum=new Text();
			    long t=(info.getTime().getTime()/1000l);
			    		
			    valuenum.set(info.getLocal()+"|"+String.valueOf(t));
			    keytext.set(outkey.toString());
				outinfo.setOutkey(keytext);
				outinfo.setOutvalue(valuenum);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			outinfo.setOutValidate(false);
			return outinfo;
		}
			
		 return outinfo;
	 }
	 
	 
	 
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getUpdatetype() {
		return updatetype;
	}
	public void setUpdatetype(String updatetype) {
		this.updatetype = updatetype;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}



	public boolean isValidate() {
		return validate;
	}



	public void setValidate(boolean validate) {
		this.validate = validate;
	}



	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}
	 
	 
	
}
