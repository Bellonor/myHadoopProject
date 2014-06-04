package mysequence.machineleaning.association.fpgrowth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
			LinkedList<String> linst=Transaction.itemsort(items);
			if(root.getChildren()==null)addNode(root,linst);
			if(root.getChildren()!=null){
				addNode2(root,linst);
			}
			String ss="dd";
		}
	}
	//当已经有分枝存在的时候，判断新来的节点是否属于该分枝的某个节点，或全部重合，递归
	public static TreeNode addNode2(TreeNode root,LinkedList<String> linst){
		if(linst.size()<=0)return null;
		String item=linst.poll();
		//当前节点的孩子节点不包含该节点，那么另外创建一支分支。
		if(root.findChild(item)==null){
			
		}
		return root;
	}
	//为空树添加第一个分枝,新建分支
	public static TreeNode addNode(TreeNode root,LinkedList<String> linst){
		
		if(linst.size()<=0)return null;
		String item=linst.poll();
		TreeNode node=new TreeNode();
		node.setName(item);
		node.setCount(1);
		node.setParent(root);
		root.addChild(node);
		
		addNode(node,linst);
		return root;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//读取数据
		ReadData.readF1(); 
		//获取F1
		getF1();
		String str[]=new String[]{"I1","I2","I5"};
		//构建树
		buildFPTree();
	}

}
