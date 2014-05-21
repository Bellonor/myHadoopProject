package myApriori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
/*本例为《数据挖掘:概念与技术(第1版)》 韩家炜,第六章apriori伪代码实现 */
public class MyApriori {

	public static final int  support = 5; // 设定最小支持频次为2 
	public static  int  itemnum = 0; // 设定购物篮中最大项数
	
	public static Map<String,String> init(){
		 // 初始化事务集  
        Map<String,String> map=new HashMap<String,String>();
        map.put("T100","I1,I2,I5");
        map.put("T200", "I2,I4");
        map.put("T300", "I2,I3");
        map.put("T400","I1,I2,I4" );
        map.put("T500","I1,I3" );
        map.put("T600", "I2,I3");
        map.put("T700", "I1,I3");
        map.put("T800", "I1,I2,I3,I5");
        map.put("T900", "I1,I2,I3");
        return map;
   }
	/**
	 * 扫描事务集以确定频繁1项集(找出C1)
	 */
	public static TreeMap<String,Integer> findFrequentOneItemSets(Map<String,String> map){
		TreeMap<String,Integer> treemap=new TreeMap<String,Integer>();
		Iterator<Entry<String,String>> iter=map.entrySet().iterator();
		Entry<String,String> entry;
		while(iter.hasNext()){
			entry=iter.next();
			String str=entry.getValue();
			if(str.length()<1)continue;
			String[] items=str.split(",");
			if(items.length>itemnum)itemnum=items.length;
			for(int i=0;i<items.length;i++){
				if(treemap.containsKey(items[i])){
					Integer count=treemap.get(items[i])+1;
					treemap.put(items[i], count);
				}else{
					treemap.put(items[i], 1);
				}
			}
		}
		return generateCandidates(treemap,support);
	}
	/**
	 * 剪枝:产生候选项，删除最小事务支持的选项
	 * */
	public static TreeMap<String,Integer> generateCandidates(TreeMap<String,Integer> map,int support){
		TreeMap<String,Integer> treemap=new TreeMap<String,Integer>();
		Iterator<Entry<String,Integer>> iter=map.entrySet().iterator();
		Entry<String,Integer> entry;
		while(iter.hasNext()){
			entry=iter.next();
			if(entry.getValue()>support){
				treemap.put(entry.getKey(), entry.getValue());
			}
		}
		return treemap;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TreeMap<String,Integer> treemap=findFrequentOneItemSets(init());
		String dd="Dd";
	}

}
