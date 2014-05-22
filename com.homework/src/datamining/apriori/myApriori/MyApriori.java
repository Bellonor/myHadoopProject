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

	public static final int  support = 2; // 设定最小支持频次为2 
	public static  int  itemnum = 0; // 设定购物篮中最大项数
	public static Map<String,String> dataMap=init();
	
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
	public static List<ItemMap> findFrequentOneItemSets(Map<String,String> map){
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
		return DeleteItem(treemap);
	}
	/**
	 * 剪枝:产生候选项，删除最小事务支持的选项
	 * */
	public static List<ItemMap> DeleteItem(TreeMap<String,Integer> map){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		Iterator<Entry<String,Integer>> iter=map.entrySet().iterator();
		Entry<String,Integer> entry;
		while(iter.hasNext()){
			entry=iter.next();
			if(entry.getValue()>=support){
				ItemMap item=new ItemMap();
				item.setKey(entry.getKey());
				item.setValue(entry.getValue());
				listmap.add(item);
			}
		}
		return listmap;
	}
	//对非初始项进行剪枝
	public static List<ItemMap> DeleteItem2(List<ItemMap> limap){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		for(int i=0;i<limap.size();i++){
			if(limap.get(i).getValue()>=support){
				ItemMap item=new ItemMap();
				item.setKey(limap.get(i).getKey());
				item.setValue(limap.get(i).getValue());
				listmap.add(item);
			}
		}
		limap.clear();
		return listmap;
	}
	
	//计数,该子集在购物篮中出现的次数
	public static List<ItemMap> countItems( List<ItemMap> list){
		List<ItemMap> listmap=new ArrayList<ItemMap>();
		
		//按传入来的组合项集进行循环
		for(int i=0;i<list.size();i++){
			ItemMap itemap=list.get(i);
			String items[]=itemap.getKey().split(",");
			ItemMap itemap2=new ItemMap();
			itemap2.setKey(itemap.getKey());
			//取出原始数据进行比较，如果购物栏中包含了该子集就+1
			Iterator<Entry<String,String>> iter=dataMap.entrySet().iterator();
			Entry<String,String> entry;
			
			while(iter.hasNext()){
				entry=iter.next();
				boolean iscontain=true;
				String itemstr=entry.getValue();
				for(int j=0;j<items.length;j++){
					if(!itemstr.contains(items[j])){
						iscontain=false;
					}
				}
				if(iscontain){
					itemap2.setValue(itemap2.getValue()+1);
				}
			}
			listmap.add(itemap2);
		}
		list.clear();//释放内存
		return listmap;
	}
    /**
	 * 扫描C1产生C2,频繁项集2，产生C2的规则是，C1中的取两位组合
	 */
	public static List<ItemMap> findFrequentTwoItemSets(List<ItemMap> listmap){
		List<ItemMap> list=new ArrayList<ItemMap>();
		Integer rows=listmap.size();
		for(int i=0;i<rows-1;i++){
			for(int j=i+1;j<rows;j++){
				ItemMap item=new ItemMap();
				ItemMap item2=new ItemMap();
				String key=listmap.get(i).getKey()+","+listmap.get(j).getKey();
				item.setKey(key); 
				list.add(item);
			}
		}
		//计数然后删除不合格的项
		return DeleteItem2(countItems(list));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<ItemMap> listmap=findFrequentOneItemSets(dataMap);
		List<ItemMap> listmap2=findFrequentTwoItemSets(listmap);
		ItemMap itm=new ItemMap();
		String dd="Dd";
	}

}
