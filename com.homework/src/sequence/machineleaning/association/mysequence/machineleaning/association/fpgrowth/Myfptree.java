package mysequence.machineleaning.association.fpgrowth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mysequence.machineleaning.association.apriori.ItemMap;
import mysequence.machineleaning.association.common.ReadData;
import mysequence.machineleaning.association.common.Transaction;

public class Myfptree {

	public static final int  support = 2; // 设定最小支持频次为2 
	public static  int  itemnum = 0; // 设定购物篮中最大项数
	
	public static List<ItemMap> lif1=null;
	//public static Map<String,String> dataMap=ReadDat.dataMap;
	//获得F1
	public static void  getF1() throws IOException{
		ReadData.readF1(); 
		lif1=Transaction.findFrequentOneItemSets(ReadData.dataMap);
		Transaction.toMap();
		
	}
    //构建FP-tree
	public static void buildFPTree(){
	    TreeNode root=new TreeNode();
		Map<String,String> map= ReadData.dataMap;
		Iterator<Entry<String,String>> iter=map.entrySet().iterator();
		Entry<String,String> entry;
		while(iter.hasNext()){
			entry=iter.next();
			String[] items=entry.getValue().trim().split(",");
			items=Transaction.itemsort(items);
			for(int i=0;i<items.length;i++){
				TreeNode child=new TreeNode();
				child.setName(items[i]);
				child.setCount(1);
			}
		}
		
		
	}
	public static void addNode(){
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		getF1();
		String str[]=new String[]{"I1","I2","I5"};
		String str2[]=Transaction.itemsort(str);
	}

}
