package mysequence.machineleaning.association.apriori;

import java.util.HashMap;
import java.util.Map;

public class ItemMap {

	public String key;
	public Integer value=0;
	
	public Map<String,Integer> map;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Map<String, Integer> getMap() {
		if(map==null){
			map=new HashMap<String,Integer>();
		}
		return map;
	}
	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}
	
}
