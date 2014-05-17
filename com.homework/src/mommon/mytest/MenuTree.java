package mytest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//递归一颗树
public class MenuTree {


	public static void mytree(List<Node> nlist,Node node){
		System.out.print(node.getId()+node.getNodeName());
		Node subnode=null;
		Long id=node.getId();
		Iterator<Node> iter=nlist.iterator();
		boolean isexit=false;
		while(iter.hasNext()){
			Node nod=iter.next();
			if(nod.getParentId()==id){
				isexit=true;
				subnode=nod;
				mytree(nlist,subnode);
				
			}
		}
		if(!isexit)return;
		
	}
	
	
	public static void main(String[] args) {
            
		long start = System.currentTimeMillis();
		List<Node> nodeList = new ArrayList<Node>();
		Node node1 = new Node(1l, "蔬菜", 0l);
		Node node2 = new Node(2l, "水产", 0l);
		Node node3 = new Node(3l, "畜牧", 0l);
		Node node4 = new Node(4l, "瓜类", 1l);
		Node node5 = new Node(5l, "叶类", 1l);
		Node node6 = new Node(6l, "丝瓜", 4l);
		Node node7 = new Node(7l, "黄瓜", 4l);
		Node node8 = new Node(8l, "白菜", 1l);
		Node node9 = new Node(9l, "虾", 2l);
		Node node10 = new Node(10l, "鱼", 2l);
		Node node11 = new Node(11l, "牛", 3l);
		Node node0=new Node(0l,"市场种类",-1l);
		
		nodeList.add(node0);
		nodeList.add(node1);
		nodeList.add(node2);
		nodeList.add(node3);
		nodeList.add(node4);
		nodeList.add(node5);
		nodeList.add(node6);
		nodeList.add(node7);
		nodeList.add(node8);
		nodeList.add(node9);
		nodeList.add(node10);
		nodeList.add(node11);
		
		mytree(nodeList,node0);
		//NodeUtil mt = new NodeUtil();
		//System.out.println(mt.getChildNodes(nodeList, 1l));
		long end = System.currentTimeMillis();
		System.out.println("用时:" + (end - start) + "ms");
	}

}
