package sequence.machinelearning.decisiontree.id3test;
import java.util.*;

public class ID3 {
	//根节点
	TreeNode root;
	
	//可见性数组
	private boolean[] visiable;
	
	//未找到节点
	private static final int NO_FOUND = -1;
	
	//训练集
	private Object[] trainingArray;
	
	//节点索引
	private int nodeIndex;
	
	//主函数
	public static void main(String[] args)
	{
		//初始化训练集数组
		Object[] array = new Object[]{
				new String[]{"Sunny","Hot","High","Weak","No"},
				new String[]{"Sunny","Hot","High","Strong","No"},
				new String[]{"Overcast","Hot","High","Weak","Yes"},
				new String[]{"Rain","Mild","High","Weak","Yes"},
				new String[]{"Rain","Cool","Normal","Weak","Yes"},
				new String[]{"Rain","Cool","Normal","Strong","No"},
				new String[]{"Overcast","Cool","Normal","Strong","Yes"},
				new String[]{"Sunny","Mild","High","Weak","No"},
				new String[]{"Sunny","Cool","Normal","Weak","Yes"},
				new String[]{"Rain","Mild","Normal","Weak","Yes"},
				new String[]{"Sunny","Mild","Normal","Strong","Yes"},
				new String[]{"Overcast","Mild","High","Strong","Yes"},
				new String[]{"Overcast","Hot","Normal","Weak","Yes"},
				new String[]{"Rain","Mild","High","Strong","No"}};
		
		//建决策树
		ID3 ID3Tree = new ID3();
		
		//这里array是训练集数组，4是训练集属性的个数
		ID3Tree.create(array, 4);
		System.out.println("==========END PRINT TREE==========");
		
		//测试数据
		String[] printData = new String[]{"Overcast","Cool","Normal","Weak"};
		
		System.out.println("==========DECISION RESULT==========");
		
		//预测
		ID3Tree.compare(printData, ID3Tree.root);
	}
	
	//根据传入数据进行预测
	public void compare(String[] printData, TreeNode node)
	{
		int index = getNodeIndex(node.nodeName);
		if (index == NO_FOUND)
		{
			System.out.println(node.nodeName);
			System.out.println((node.percent * 100) + "%");
		}
		TreeNode[] childs = node.childNodes;
		for (int i = 0; i <childs.length; i++)
		{
			if (childs[i] != null)
			{
				if (childs[i].parentAttribute.equals(printData[index]))
				{
					compare(printData,childs[i]);
				}
			}
		}
	}
	
	//创建
	public void create(Object[] array, int index)
	{
		this.trainingArray = array;
		init(array, index);
		createDTree(array);
		printDTree(root);
	}
	
	//初始化
	public void init(Object[] dataArray, int index)
	{
		this.nodeIndex = index;
		
		//数据初始化
		this.visiable = new boolean[((String[])dataArray[0]).length];
		for (int i = 0; i<this.visiable.length; i++)
		{
			if (i == index)
			{
				this.visiable[i] = true;
			}
			else
			{
				this.visiable[i] = false;
			}
		}
	}
	
	//创建决策树
	public void createDTree(Object[] array)
	{
		Object[] maxgain = getMaxGain(array);
		if (this.root == null)
		{
			this.root = new TreeNode();
			root.parent = null;
			root.parentAttribute = null;
			root.attributes = getAttributes(((Integer)maxgain[1]).intValue());
			root.nodeName = getNodeName(((Integer)maxgain[1]).intValue());
			root.childNodes = new TreeNode[root.attributes.length];
			insertTree(array, root);
		}
	}
	
	//插入决策树
	public void insertTree(Object[] array, TreeNode parentNode)
	{
		String[] attributes = parentNode.attributes;
		for (int i = 0; i < attributes.length; i++)
		{
			Object[] pickArray = pickUpAndCreateArray(array, attributes[i],getNodeIndex(parentNode.nodeName));
			Object[] info = getMaxGain(pickArray);
			double gain = ((Double)info[0]).doubleValue();
			if (gain != 0)
			{
				int index = ((Integer)info[1]).intValue();
				TreeNode currentNode = new TreeNode();
				currentNode.parent = parentNode;
				currentNode.parentAttribute = attributes[i];
				currentNode.attributes = getAttributes(index);
				currentNode.nodeName = getNodeName(index);
				currentNode.childNodes = new TreeNode[currentNode.attributes.length];
				parentNode.childNodes[i] = currentNode;
				insertTree(pickArray, currentNode);
			}
			else
			{
				TreeNode leafNode = new TreeNode();
				leafNode.parent = parentNode;
				leafNode.parentAttribute = attributes[i];
				leafNode.attributes = new String[0];
				leafNode.nodeName = getLeafNodeName(pickArray);
				leafNode.childNodes = new TreeNode[0];
				parentNode.childNodes[i] = leafNode;
				
				double percent = 0;
				String[] arrs = getAttributes(this.nodeIndex);
				for (int j = 0; j < arrs.length; j++)
				{
					if (leafNode.nodeName.equals(arrs[j]))
					{
						Object[] subo = pickUpAndCreateArray(pickArray,arrs[j],this.nodeIndex);
						Object[] o = pickUpAndCreateArray(this.trainingArray,arrs[i],this.nodeIndex);
						double subCount = subo.length;
						percent = subCount / o.length;
					}
				}
				leafNode.percent = percent;
			}
		}
	}
	
	//输出决策树
	public void printDTree(TreeNode node)
	{
		System.out.println(node.nodeName);
		TreeNode[] childs = node.childNodes;
		for (int i = 0; i < childs.length; i++)
		{
			if (childs[i] != null)
			{
				System.out.println(childs[i].parentAttribute);
				printDTree(childs[i]);
			}
		}
	}
	
	//剪取数组
	public Object[] pickUpAndCreateArray(Object[] array, String attribute, int index)
	{
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < array.length; i++)
		{
			String[] strs = (String[])array[i];
			if (strs[index].equals(attribute))
			{
				list.add(strs);
			}
		}
		return list.toArray();
	}
	
	//取得节点名
	public String getNodeName(int index)
	{
		String[] strs = new String[]{"Outlook","Temperature","Humidity","Wind","PlayTennis"};
		for (int i = 0; i < strs.length; i++)
		{
			if (i == index)
			{
				return strs[i];
			}
		}
		return null;
	}
	
	//取得叶子节点名
	public String getLeafNodeName(Object[] array)
	{
		if (array != null && array.length > 0)
		{
			String[] strs = (String[])array[0];
			return strs[nodeIndex];
		}
		return null;
	}
	
	//取得节点索引
	public int getNodeIndex(String name)
	{
		String[] strs = new String[]{"Outlook","Temperature","Humidity","Wind","PlayTennis"};
		for (int i = 0; i < strs.length; i++)
		{
			if (name.equals(strs[i]))
			{
				return i;
			}
		}
		return NO_FOUND;
	}
	
	
	
	//得到最大信息增益
	public Object[] getMaxGain(Object[] array)
	{
		Object[] result = new Object[2];
		double gain = 0;
		int index = -1;
		for (int i = 0; i<this.visiable.length; i++)
		{
			if (!this.visiable[i])
			{
				double value = gain(array, i);
				if (gain < value)
				{
					gain = value;
					index = i;
				}
			}
		}
		result[0] = gain;
		result[1] = index;
		if (index != -1)
		{
			this.visiable[index] = true;
		}
		return result;
	}
	
	//取得属性数组
	public String[] getAttributes(int index)
	{
		TreeSet<String> set = new TreeSet<String>(new SequenceComparator());
		for (int i = 0; i<this.trainingArray.length; i++)
		{
			String[] strs = (String[])this.trainingArray[i];
			set.add(strs[index]);
		}
		String[] result = new String[set.size()];
		return set.toArray(result);
		
	}
	
	//计算信息增益
	public double gain(Object[] array, int index)
	{
		String[] playBalls = getAttributes(this.nodeIndex);
		int[] counts = new int[playBalls.length];
		for (int i = 0; i<counts.length; i++)
		{
			counts[i] = 0;
		}
		
		for (int i = 0; i<array.length; i++)
		{
			String[] strs = (String[])array[i];
			for (int j = 0; j<playBalls.length; j++)
			{	
				if (strs[this.nodeIndex].equals(playBalls[j]))
				{
					counts[j]++;
				}
			}
		}
		
		double entropyS = 0;
		for (int i = 0;i <counts.length; i++)
		{
			entropyS = entropyS + DTreeUtil.sigma(counts[i], array.length);
		}
		
		String[] attributes = getAttributes(index);
		double sv_total = 0;
		for (int i = 0; i<attributes.length; i++)
		{
			sv_total = sv_total + entropySv(array, index, attributes[i], array.length);
		}
		return entropyS - sv_total;
	}
	
	
	public double entropySv(Object[] array, int index, String attribute, int allTotal)
	{
		String[] playBalls = getAttributes(this.nodeIndex);
		int[] counts = new int[playBalls.length];
		for (int i = 0; i < counts.length; i++)
		{
			counts[i] = 0;
		}
		
		for (int i = 0; i < array.length; i++)
		{
			String[] strs = (String[])array[i];
			if (strs[index].equals(attribute))
			{
				for (int k = 0; k<playBalls.length; k++)
				{
					if (strs[this.nodeIndex].equals(playBalls[k]))
					{
						counts[k]++;
					}
				}
			}
		}
		
		int total = 0;
		double entropySv = 0;
		for (int i = 0; i < counts.length; i++)
		{
			total = total +counts[i];
		}
		
		for (int i = 0; i < counts.length; i++)
		{
			entropySv = entropySv + DTreeUtil.sigma(counts[i], total);
		}
		return DTreeUtil.getPi(total, allTotal)*entropySv;
	}
}