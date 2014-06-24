package sequence.machinelearning.decisiontree.id3test;

public class TreeNode {
	//父节点
	TreeNode parent;
	
	//指向父的哪个属性
	String parentAttribute;
	
	//节点名
	String nodeName;
	
	//属性数组
	String[] attributes;
	
	//节点数组
	TreeNode[] childNodes;
	
	//可信度
	double percent;
}
