package mysequence.machineleaning.association.fpgrowth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	public static TreeNode root=new TreeNode();
	public static List<ItemMap> lif1=null;
	//链头
	public static Map<String,LinkedList<TreeNode>> maplin=new HashMap<String,LinkedList<TreeNode>>();
	//保存条件模式基的数据
	public static List<ItemMap> conditionMode=new ArrayList<ItemMap>();
	//public static Map<String,String> dataMap=ReadDat.dataMap;
	//获得F1
	public static void  getF1() throws IOException{
		
		lif1=Transaction.findFrequentOneItemSets(ReadData.dataMap);
		Transaction.toMap();
		
	}
	//寻找条件模式基，寻找条件模式基后，本例采用排列组合的方法取得频繁模式
	public static void  findleaf(){
		
		Iterator<Entry<String,LinkedList<TreeNode>>> iter=maplin.entrySet().iterator();
		Entry<String,LinkedList<TreeNode>> entry;
		while(iter.hasNext()){
			entry=iter.next();
			for(TreeNode node:entry.getValue()){
				
			}
            
		}
		String ss="dd";
	}
	//从叶子找到根节点，递归之
	public static void findroot(TreeNode node){
		
	}
	
    //构建FP-tree
	public static void buildFPTree(){
	   
		Map<String,String> map= ReadData.dataMap;
		Iterator<Entry<String,String>> iter=map.entrySet().iterator();
		Entry<String,String> entry;
		while(iter.hasNext()){
			entry=iter.next();
			String[] items=entry.getValue().trim().split(",");
			LinkedList<String> linst=Transaction.itemsort(items);
			addNode(root,linst);
            
		}
		String ss="dd";
	}
	//当已经有分枝存在的时候，判断新来的节点是否属于该分枝的某个节点，或全部重合，递归
	public static TreeNode addNode(TreeNode root,LinkedList<String> linst){
		if(linst.size()<=0)return null;
		String item=linst.poll();
		//当前节点的孩子节点不包含该节点，那么另外创建一支分支。
		TreeNode node=root.findChild(item);
		if(node==null){
			node=new TreeNode();
			node.setName(item);
			node.setCount(1);
			node.setParent(root);
			root.addChild(node);
		}else{
			node.setCount(node.getCount()+1);
		}
		//加将各个节点加到结点链中 
		if(maplin.containsKey(item)){
			if(!maplin.get(item).contains(node)){
				maplin.get(item).add(node);
			}
		}else{
			LinkedList<TreeNode> lin=new LinkedList<TreeNode>();
			lin.add(node);
			maplin.put(item, lin);
		}
		//加将各个节点加到结点链中 
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
/*我用的是递归解决了这个问题。
代码如下：
private void bianliadd(TreeNodeCollection tc)
{

foreach( TreeNode TNode in tc)
{
if (TNode.Text == "长城")
{
MessageBox.show("ok!") 
}
else
{
bianliadd(TNode.Nodes);
}
}*/