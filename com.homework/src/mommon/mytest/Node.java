package mytest;

/**
 * 无限级节点模型
 */
public class Node {
	/**
	 * 节点id
	 */
	private Long id;

	/**
	 * 节点名称
	 */
	private String nodeName;

	/**
	 * 父节点id
	 */
	private Long parentId;

	public Node() {
	}

	Node(Long id, Long parentId) {
		this.id = id;
		this.parentId = parentId;
	}

	Node(Long id, String nodeName, Long parentId) {
		this.id = id;
		this.nodeName = nodeName;
		this.parentId = parentId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}

