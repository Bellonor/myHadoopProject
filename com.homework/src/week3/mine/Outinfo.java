package mine;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class Outinfo {

	 private Text outkey;
	 private Text outvalue;
	 private boolean outValidate=true;
	 
	public Text getOutkey() {
		return outkey;
	}
	public void setOutkey(Text outkey) {
		this.outkey = outkey;
	}
	
	public Text getOutvalue() {
		return outvalue;
	}
	public void setOutvalue(Text outvalue) {
		this.outvalue = outvalue;
	}
	public boolean isOutValidate() {
		return outValidate;
	}
	public void setOutValidate(boolean outValidate) {
		this.outValidate = outValidate;
	} 
	 
	 
}
