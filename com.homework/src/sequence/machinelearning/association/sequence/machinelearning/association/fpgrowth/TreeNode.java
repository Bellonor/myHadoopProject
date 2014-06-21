package sequence.machinelearning.association.fpgrowth;

import java.util.ArrayList;
import java.util.List;





public class TreeNode {
	private String name; // 结点名称
	private Integer count; // 项目支持统计计数
	private List<TreeNode> children; // 指向下一个具有相同nodeName的结点指针
	private TreeNode parent; // 结点的直接父结点，根结点的直接孩子结点的nodeParent为null
	
	

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

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	
	
}
