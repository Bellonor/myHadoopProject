package sequence.machinelearning.decisiontree.myid3;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private String name; // 节点名称
	private TreeNode parent; // 父节点
	private List<TreeNode> children; // 子节点
	private String fatherAttribute; // 此节点是父类的哪具属性的分支
	//可信度
	private Double percent;
	
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TreeNode getParent() {
		return parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
	public String getFatherAttribute() {
		return fatherAttribute;
	}
	public void setFatherAttribute(String fatherAttribute) {
		this.fatherAttribute = fatherAttribute;
	}
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	/**
     * 添加一个节点
     * @param child
     */
    public void addChild(TreeNode child) {
        if (this.getChildren() == null) {
            List<TreeNode> list = new ArrayList<TreeNode>();
            list.add(child);
            this.setChildren(list);
        } else {
            this.getChildren().add(child);
        }
    }
    /**
    *  是否存在着该节点,存在返回该节点，不存在返回空
    * @param name
    * @return
    */
    public TreeNode findChild(String name) {
        List<TreeNode> children = this.getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }
	
}
