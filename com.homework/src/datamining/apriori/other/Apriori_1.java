package other;

import java.util.*;

public class Apriori_1 {

	private double minsup = 0.6;// 最小支持度
	private double minconf = 0.2;// 最小置信度

	// 注意使用IdentityHashMap，否则由于关联规则产生存在键值相同的会出现覆盖
	private IdentityHashMap ruleMap = new IdentityHashMap();

	private String[] transSet = { "abc", "abc", "acde", "bcdf", "abcd", "abcdf" };// 事务集合
																					// ，
																					// 可以根据需要从构造函数里传入

	private int itemCounts = 0;// 候选1项目集大小,即字母的个数
	private TreeSet[] frequencySet = new TreeSet[40];// 频繁项集数组，[0]:代表1频繁集...，TreeSet（）使用元素的自然顺序对元素进行排序
	private TreeSet maxFrequency = new TreeSet();// 最大频繁集
	private TreeSet candidate = new TreeSet();
	private TreeSet candidateSet[] = new TreeSet[40];// 候选集数组[0]:代表1候选集
	private int frequencyIndex;

	public Apriori_1() {

		maxFrequency = new TreeSet();
		itemCounts = counts();// 初始化1候选集的大小6个

		// 初始化其他两个
		for (int i = 0; i < itemCounts; i++) {
			frequencySet[i] = new TreeSet();//初始化频繁项集数组
			candidateSet[i] = new TreeSet();//初始化候选集数组
		}
		candidateSet[0] = candidate;// 1候选集
	}

	//主函数入口
	public static void main(String[] args) {
		Apriori_1 ap = new Apriori_1();
		ap.run();
	}
	
	//方法运行
	public void run() {
		int k = 1;
		item1_gen();

		do {
			k++;
			canditate_gen(k);
			frequent_gen(k);
		} while (!is_frequent_empty(k));
		frequencyIndex = k - 1;
		print_canditate();
		maxfrequent_gen();
		print_maxfrequent();
		ruleGen();
		rulePrint();
	}
	//记录每个事务中的元素出现次数
	public double count_sup(String x) {
		int temp = 0;
		for (int i = 0; i < transSet.length; i++) {
			for (int j = 0; j < x.length(); j++) {
				if (transSet[i].indexOf(x.charAt(j)) == -1)//返回指定字符在此字符串中第一次出现处的索引，如果不作为一个字符串，返回-1
					break;
				else if (j == (x.length() - 1))
					temp++;
			}
		}
		return temp;
	}
	
	//统计1候选集的个数a,b,c,d,e,f,return值为6
	public int counts() {

		String temp1 = null;
		char temp2 = 'a';
		// 遍历所有事务集String 加入集合，set自动去重了
		for (int i = 0; i < transSet.length; i++) {
			temp1 = transSet[i];
			for (int j = 0; j < temp1.length(); j++) {
				temp2 = temp1.charAt(j);//返回位置为j的temp1的值a
				candidate.add(String.valueOf(temp2));//treeSet添加会去掉重复的值
			}
		}
		return candidate.size();//中元素个数不重复，且递增排序
	}

	//求1频繁集
	public void item1_gen() {
		String temp1 = "";
		double m = 0;

		Iterator temp = candidateSet[0].iterator();//使用方法iterator()要求容器返回一个Iterator。
		while (temp.hasNext()) {//遍历temp（1候选集）
			temp1 = (String) temp.next();
			m = count_sup(temp1);//调用下面的方法，统计1候选集中每个元素个数，计算支持度时，用此m/transSet.length

			// 符合条件的加入 1候选集
			if (m >= minsup * transSet.length) {//minsup * transSet.length的值为记录每个事务中的元素出现次数,判断是否1频繁集
				frequencySet[0].add(temp1);//1频繁集加入频繁项集数组，自动出去重复的集合
			}
		}
	}
	//求K候选集
	public void canditate_gen(int k) {
		String y = "", z = "", m = "";
		char c1 ,c2 ;

		Iterator temp1 = frequencySet[k - 2].iterator();//iterator迭代器，用于数组遍历
		Iterator temp2 = frequencySet[0].iterator();//遍历频繁项集数组，[0]:代表1频繁集
		TreeSet h = new TreeSet();

		while (temp1.hasNext()) {
			y = (String) temp1.next();//
			c1 = y.charAt(y.length() - 1);//返回指定y.length() - 1（数组的最后一个）的char值

			while (temp2.hasNext()) {
				z = (String) temp2.next();

				c2 = z.charAt(0);//c2=a,b,c,d,e,f
				if (c1 >= c2)
					continue;
				else {
					m = y + z;//m为字符串组合yz
					h.add(m);//m加入TreeSet
				}
			}
			temp2 = frequencySet[0].iterator();
		}
		candidateSet[k - 1] = h;
	}

	// k候选集=>k频繁集
	public void frequent_gen(int k) {
		String s1 = "";

		Iterator ix = candidateSet[k - 1].iterator();//遍历K候选集ix
		while (ix.hasNext()) {
			s1 = (String) ix.next();//ix中的值s1
			if (count_sup(s1) >= (minsup * transSet.length)) {//s1项集支持度大于最小支持度
				frequencySet[k - 1].add(s1);//s1加入K频繁集中
			}
		}
	}
    //判断频繁集为空
	public boolean is_frequent_empty(int k) {
		if (frequencySet[k - 1].isEmpty())
			return true;
		else
			return false;
	}
	//打印候选集 频繁集
	public void print_canditate() {

		for (int i = 0; i < frequencySet[0].size(); i++) {
			Iterator ix = candidateSet[i].iterator();
			Iterator iy = frequencySet[i].iterator();
			System.out.print("候选集" + (i + 1) + ":");
			while (ix.hasNext()) {
				System.out.print((String) ix.next() + "\t");
			}
			System.out.print("\n" + "频繁集" + (i + 1) + ":");
			while (iy.hasNext()) {
				System.out.print((String) iy.next() + "\t");
			}
			System.out.println();
		}
	}

    //求关联项集合
	public void maxfrequent_gen() {
		int i;
		for (i = 1; i < frequencyIndex; i++) {
			maxFrequency.addAll(frequencySet[i]);
		}
	}
    //打印频繁项集
	public void print_maxfrequent() {
		Iterator iterator = maxFrequency.iterator();
		System.out.print("频繁项集：");
		while (iterator.hasNext()) {
			System.out.print(((String) iterator.next()) + "\t");
		}
		System.out.println();
		System.out.println();
	}
	//关联规则项集
	public void ruleGen() {
		String s;
		Iterator iterator = maxFrequency.iterator();
		while (iterator.hasNext()) {
			s = (String) iterator.next();
			subGen(s);
		}
	}

    //求关联规则
	public void subGen(String s) {
		String x = "", y = "";
		for (int i = 1; i < (1 << s.length()) - 1; i++) {
			for (int j = 0; j < s.length(); j++) {
				if (((1 << j) & i) != 0) {
					x += s.charAt(j);
				}
			}

			for (int j = 0; j < s.length(); j++) {
				if (((1 << j) & (~i)) != 0) {

					y += s.charAt(j);

				}
			}
			if (count_sup(x + y) / count_sup(x) >= minconf) {
				ruleMap.put(x, y);
			}
			x = "";
			y = "";

		}
	}


	//打印关联规则
	public void rulePrint() {
		String x, y;
		float temp = 0;

		Set hs = ruleMap.keySet();//迭代后只能用get取key，Set不包含重复元素的collection
		Iterator iterator = hs.iterator();
		System.out.println("关联规则：");
		while (iterator.hasNext()) {
			x = (String) iterator.next();

			y = (String) ruleMap.get(x);

			temp = (float) (count_sup(x + y) / count_sup(x));

			System.out.println(x + (x.length() < 5 ? "\t" : "") + "-->" + y+ "\t" + "置信度: " + temp);

		}
	}




}
