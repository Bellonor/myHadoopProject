package mysequence.machineleaning.association.fpgrowth;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Myfptree2 {
	public static final int  support = 2; // 设定最小支持频次为2 
	public LinkedList<LinkedList<String>> readF1() throws IOException {      
		LinkedList<LinkedList<String>> records=new LinkedList<LinkedList<String>>();
		//String filePath="scripts/clustering/canopy/canopy.dat";
		String filePath="datafile/association/fpg2";
		BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(filePath)));
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.length()==0||"".equals(line))continue;
        	String[] str=line.split(",");   
        	LinkedList<String> litm=new LinkedList<String>();
        	for(int i=0;i<str.length;i++){
        		litm.add(str[i].trim());
        	}
            records.add(litm);             
        }
        br.close();
        return records;
    }
	//创建表头链
	public List<TreeNode2> buildHeaderLink(LinkedList<LinkedList<String>> records){
		List<TreeNode2> header=null;
		if(records.size()>0){
			header=new ArrayList<TreeNode2>();
		}else{
			return null;
		}
		Map<String, TreeNode2> map = new HashMap<String, TreeNode2>();
		for(LinkedList<String> items:records){
			
			for(String item:items){
				//如果存在数量增1，不存在则新增
				if(map.containsKey(item)){
					map.get(item).Sum(1);
				}else{
					TreeNode2 node=new TreeNode2();
					node.setName(item);
					node.setCount(1);
					map.put(item, node);
				}

			}
		}
		 // 把支持度大于（或等于）minSup的项加入到F1中
        Set<String> names = map.keySet();
        for (String name : names) {
            TreeNode2 tnode = map.get(name);
            if (tnode.getCount() >= support) {
            	header.add(tnode);
            }
        }
        sort(header);
		
        String test="ddd";
		return header;
	}
	//选择法排序
	public List<TreeNode2> sort(List<TreeNode2> list){
		int len=list.size();
		for(int i=0;i<len;i++){
			
			for(int j=i+1;j<len;j++){
				TreeNode2 node1=list.get(i);
				TreeNode2 node2=list.get(j);
				if(node1.getCount()<node2.getCount()){
					TreeNode2 tmp=new TreeNode2();
					tmp=node2;
					list.remove(j);
					//list指定位置插入，原来的>=j元素都会往下移，不会删除,所以插入前要删除掉原来的元素
					list.add(j,node1);
					list.remove(i);
					list.add(i,tmp);
				}
			}
		}
		
		return list;
	}
	//选择法排序，降序,按header给出的顺序
	public   List<String> itemsort(LinkedList<String> lis,List<TreeNode2> header){
		//List<String> list=new ArrayList<String>();
		//选择法排序
		int len=lis.size();
		for(int i=0;i<len;i++){
			for(int j=i+1;j<len;j++){
				String key1=lis.get(i);
				String key2=lis.get(j);
				Integer value1=findcountByname(key1,header);
				Integer value2=findcountByname(key2,header);
				if(value1<value2){
					String tmp=key2;
					lis.remove(j);
					lis.add(j,key1);
					lis.remove(i);
					lis.add(i,tmp);
				}
		     }
		}
		return lis;
	}
	public Integer findcountByname(String itemname,List<TreeNode2> header){
		Integer count=-1;
		for(TreeNode2 node:header){
			if(node.getName().equals(itemname)){
				count= node.getCount();
			}
		}
		return count;
	}
	
	//创建FP-树
	public TreeNode2 builderFpTree(LinkedList<LinkedList<String>> records,List<TreeNode2> header){
		
		  TreeNode2 root=new TreeNode2();
		   for(LinkedList<String> items:records){
			   itemsort(items,header);
			  addNode(root,items,header);
			}
			
		String test="ddd";
		return null;
	}
	//当已经有分枝存在的时候，判断新来的节点是否属于该分枝的某个节点，或全部重合，递归
	public  TreeNode2 addNode(TreeNode2 root,LinkedList<String> items,List<TreeNode2> header){
		if(items.size()<=0)return null;
		String item=items.poll();
		//当前节点的孩子节点不包含该节点，那么另外创建一支分支。
		TreeNode2 node=root.findChild(item);
		if(node==null){
			node=new TreeNode2();
			node.setName(item);
			node.setCount(1);
			node.setParent(root);
			root.addChild(node);
		}else{
			node.setCount(node.getCount()+1);
		}
		//加将各个节点加到结点链中 
		
		//加将各个节点加到结点链中 
		addNode(node,items,header);
		return root;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//读取数据
		Myfptree2 fpt=new Myfptree2();
		LinkedList<LinkedList<String>> records=fpt.readF1();
		//构建链头
		Myfptree2 fpg=new Myfptree2();
		List<TreeNode2> header=fpg.buildHeaderLink(records);
		//创建FP-Tree
		fpg.builderFpTree(records,header);
	}

}
