package mysequence.machineleaning.association.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import mysequence.machineleaning.association.apriori.ItemMap;

public class Transaction {

	public static final int  support = 2; // 设定最小支持频次为2 
	public static  int  itemnum = 0; // 设定购物篮中最大项数
	//放到Map中排序
	public static TreeMap<String,Integer> tmap=new TreeMap<String,Integer>();
	
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
			//找出购物栏最大的项,为循环连接做准备
			if(items.length>itemnum)itemnum=items.length;
			for(int i=0;i<items.length;i++){
				if(treemap.containsKey(items[i].trim())){
					Integer count=treemap.get(items[i].trim())+1;
					treemap.put(items[i].trim(), count);
				}else{
					treemap.put(items[i].trim(), 1);
				}
			}
		}
		return DeleteItem(treemap);
	}
	
	//放到Map中排序
	public static void toMap() throws IOException{
	   //ReadData.readF1(); 
	   List<ItemMap> lif1=Transaction.findFrequentOneItemSets(ReadData.dataMap);
	   for(int i=0;i<lif1.size();i++){
		   ItemMap item=lif1.get(i);
		   tmap.put(item.getKey(), item.getValue());
	   }
	}
	public static  LinkedList<String> itemsort(String[] items){
		LinkedList<String> linst=new LinkedList<String>();
		//选择法排序
		int len=items.length;
		for(int i=0;i<len;i++){
			
			for(int j=i+1;j<len;j++){
				if(!tmap.containsKey(items[i]))continue;
				if(!tmap.containsKey(items[j]))continue;
				int num=tmap.get(items[i]);
				int nextnum=tmap.get(items[j]);
				if(num<nextnum){
					String tmp=items[i];
					items[i]=items[j];
					items[j]=tmp;
				}
			}
			linst.add(items[i]);
		}
		return linst;
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
				if(listmap.size()==0)listmap.add(item);
				else{
					
					ItemMap tail=new ItemMap();
					int size=listmap.size();
					tail=listmap.get(size-1);
					if(item.getValue()>tail.getValue()){
					    listmap.remove(size-1);
					    listmap.add(item);
					    listmap.add(tail);
					}else{
						listmap.add(item);
					}
					
				  }
				
			}
		}
		return listmap;
	}
}
