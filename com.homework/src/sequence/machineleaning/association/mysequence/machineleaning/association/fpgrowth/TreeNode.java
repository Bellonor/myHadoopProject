package mysequence.machineleaning.association.fpgrowth;

public class TreeNode {
	private String name; // 结点名称
	private Integer count; // 项目支持统计计数
	private TreeNode child; // 指向下一个具有相同nodeName的结点指针
	private TreeNode parent; // 结点的直接父结点，根结点的直接孩子结点的nodeParent为null
	
	public TreeNode(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public TreeNode getChild() {
		return child;
	}

	public void setChild(TreeNode child) {
		this.child = child;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	
	
}
