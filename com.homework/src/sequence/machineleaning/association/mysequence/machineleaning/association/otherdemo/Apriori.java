package mysequence.machineleaning.association.otherdemo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
/*数据挖掘:概念与技术(第1版) 韩家炜,第六章*/
public class Apriori {

	public static void main(String[] args) throws Exception {

		// 初始化事务集
		List<Set<String>> trans = new LinkedList<Set<String>>();
		trans.add(new ItemSet(new String[] { "I1", "I2", "I5" }));
		trans.add(new ItemSet(new String[] { "I2", "I4" }));
		trans.add(new ItemSet(new String[] { "I2", "I3" }));
		trans.add(new ItemSet(new String[] { "I1", "I2", "I4" }));
		trans.add(new ItemSet(new String[] { "I1", "I3" }));
		trans.add(new ItemSet(new String[] { "I2", "I3" }));
		trans.add(new ItemSet(new String[] { "I1", "I3" }));
		trans.add(new ItemSet(new String[] { "I1", "I2", "I3", "I5" }));
		trans.add(new ItemSet(new String[] { "I1", "I2", "I3" }));

		int MSF = 2; // 设定最小支持频次为2

		Map<Integer, Set<ItemSet>> rst = findFrequentItemSets(trans, MSF);

		// 输出频繁项集
		System.out.println("Frequent Item Sets:");
		for (Entry<Integer, Set<ItemSet>> entry : rst.entrySet()) {
			Integer itemSetSize = entry.getKey();
			System.out.printf("Frequent %d Item Sets:\n", itemSetSize);
			for (ItemSet set : entry.getValue())
				System.out.printf("%s, %d\n", set, set.frequence);
		}

		double MCONF = 0.6; // 设定最小置信度为60%

		Map<ItemSet, ItemSet> directMap = new HashMap<ItemSet, ItemSet>();
		for (Entry<Integer, Set<ItemSet>> entry : rst.entrySet()) {
			for (ItemSet set : entry.getValue())
				directMap.put(set, set);
		}

		// 根据频繁项集构造关联规则
		System.out.println();
		System.out.println("Association Rules:");
		for (Entry<Integer, Set<ItemSet>> entry : rst.entrySet()) {
			for (ItemSet set : entry.getValue()) {
				double cnt1 = directMap.get(set).frequence;
				List<ItemSet> subSets = set.listNotEmptySubItemSets();
				for (ItemSet subSet : subSets) {
					int cnt2 = directMap.get(subSet).frequence;
					double conf = cnt1 / cnt2;
					if (cnt1 / cnt2 >= MCONF) {
						ItemSet remainSet = new ItemSet();
						remainSet.addAll(set);
						remainSet.removeAll(subSet);
						System.out.printf("%s => %s, %.2f\n", subSet,
								remainSet, conf);
					}
				}
			}
		}
	}

	/**
	 * 查找事务集中的所有频繁项集，返回Map为：L -> 所有频繁L项集的列表
	 */
	static Map<Integer, Set<ItemSet>> findFrequentItemSets(
			Iterable<Set<String>> transIterable, int MSF) {
		Map<Integer, Set<ItemSet>> ret = new TreeMap<Integer, Set<ItemSet>>();

		// 首先确定频繁1项集
		Iterator<Set<String>> it = transIterable.iterator();
		Set<ItemSet> oneItemSets = findFrequentOneItemSets(it, MSF);
		ret.put(1, oneItemSets);

		int preItemSetSize = 1;
		Set<ItemSet> preItemSets = oneItemSets;

		// 基于获得的所有频繁L-1项集迭代查找所有频繁L项集，直到不存在频繁L-1项集
		while (!preItemSets.isEmpty()) {
			int curItemSetSize = preItemSetSize + 1;

			// 获取频繁L项集的所有候选L项集
			List<ItemSet> candidates = aprioriGenCandidates(preItemSets);

			// 扫描事务集以确定所有候选L项集出现的频次
			it = transIterable.iterator();
			while (it.hasNext()) {
				Set<String> tran = it.next();
				for (ItemSet candidate : candidates)
					if (tran.containsAll(candidate))
						candidate.frequence++;
			}

			// 将出现频次不小于最小支持频次的候选L项集选为频繁L项集
			Set<ItemSet> curItemSets = new HashSet<ItemSet>();
			for (ItemSet candidate : candidates)
				if (candidate.frequence >= MSF)
					curItemSets.add(candidate);
			if (!curItemSets.isEmpty())
				ret.put(curItemSetSize, curItemSets);

			preItemSetSize = curItemSetSize;
			preItemSets = curItemSets;
		}
		return ret;
	}

	/**
	 * 扫描事务集以确定频繁1项集
	 */
	static Set<ItemSet> findFrequentOneItemSets(Iterator<Set<String>> trans,
			int MSF) {

		// 扫描事务集以确定各个项出现的频次
		Map<String, Integer> frequences = new HashMap<String, Integer>();
		while (trans.hasNext()) {
			Set<String> tran = trans.next();
			for (String item : tran) {
				Integer frequence = frequences.get(item);
				frequence = frequence == null ? 1 : frequence + 1;
				frequences.put(item, frequence);
			}
		}

		// 用每个出现频次不小于最小支持频次的项构造一个频繁1项集
		Set<ItemSet> ret = new HashSet<ItemSet>();
		for (Entry<String, Integer> entry : frequences.entrySet()) {
			String item = entry.getKey();
			Integer frequence = entry.getValue();
			if (frequence >= MSF) {
				ItemSet set = new ItemSet(new String[] { item });
				set.frequence = frequence;
				ret.add(set);
			}
		}
		return ret;
	}

	/**
	 * 根据所有频繁L-1项集获得所有频繁L项集的候选L项集
	 */
	static List<ItemSet> aprioriGenCandidates(Set<ItemSet> preItemSets) {
		List<ItemSet> ret = new LinkedList<ItemSet>();

		// 尝试将所有频繁L-1项集两两连接然后作剪枝处理以获得候选L项集
		for (ItemSet set1 : preItemSets) {
			for (ItemSet set2 : preItemSets) {
				if (set1 != set2 && set1.canMakeJoin(set2)) {

					// 连接
					ItemSet union = new ItemSet();
					union.addAll(set1);
					union.add(set2.last());

					// 剪枝
					boolean missSubSet = false;
					List<ItemSet> subItemSets = union.listDirectSubItemSets();
					for (ItemSet itemSet : subItemSets) {
						if (!preItemSets.contains(itemSet)) {
							missSubSet = true;
							break;
						}
					}
					if (!missSubSet)
						ret.add(union);
				}
			}
		}
		return ret;
	}

	/**
	 * 由多个项组成的项集，每个项是一个字符串。使用TreeSet使项集中的项有序，以辅助算法实现
	 */
	static class ItemSet extends TreeSet<String> {

		private static final long serialVersionUID = 23883315835136949L;

		int frequence; // 项集出现的频次

		public ItemSet() {
			this(new String[0]);
		}

		public ItemSet(String[] items) {
			for (String item : items)
				add(item);
		}

		/**
		 * 测试本项集（假定阶为L-1）能否与别一个项集连接以生成L阶项集
		 */
		public boolean canMakeJoin(ItemSet other) {

			// 若两个项集的阶不同，则不能连接生成L阶项集
			if (other.size() != this.size())
				return false;

			// 假定项集的阶为L-1，在项有序的前提下，当且仅当两个项集的前L-2个项相同
			// 而本项集的第L-1个项小于另一个项集的第L-1个项时，可以连接生成L阶项集
			Iterator<String> it1 = this.iterator();
			Iterator<String> it2 = other.iterator();
			while (it1.hasNext()) {
				String item1 = it1.next();
				String item2 = it2.next();
				int result = item1.compareTo(item2);
				if (result != 0) {
					if (it1.hasNext())
						return false;
					return result < 0 ? true : false;
				}
			}
			return false;
		}

		/**
		 * 假定本项集的阶为L，列举本项集的所有阶为L-1的子项集
		 */
		public List<ItemSet> listDirectSubItemSets() {
			List<ItemSet> ret = new LinkedList<ItemSet>();

			// 只有本项集的阶大于1，才可能存在非空子项集
			if (size() > 1) {
				for (String rmItem : this) {
					ItemSet subSet = new ItemSet();
					subSet.addAll(this);
					subSet.remove(rmItem);
					ret.add(subSet);
				}
			}

			return ret;
		}

		/**
		 * 列出本项集除自身外的所有非空子项集
		 */
		public List<ItemSet> listNotEmptySubItemSets() {
			List<ItemSet> ret = new LinkedList<ItemSet>();
			int size = size();
			if (size > 0) {
				char[] mapping = new char[size()];
				initMapping(mapping);
				while (nextMapping(mapping)) {
					ItemSet set = new ItemSet();
					Iterator<String> it = this.iterator();
					for (int i = 0; i < size; i++) {
						String item = it.next();
						if (mapping[i] == '1')
							set.add(item);
					}
					if (set.size() < size)
						ret.add(set);
				}
			}
			return ret;
		}

		private void initMapping(char[] mapping) {
			for (int i = 0; i < mapping.length; i++)
				mapping[i] = '0';
		}

		private boolean nextMapping(char[] mapping) {
			int pos = 0;
			while (pos < mapping.length && mapping[pos] == '1') {
				mapping[pos] = '0';
				pos++;
			}
			if (pos < mapping.length) {
				mapping[pos] = '1';
				return true;
			}
			return false;
		}
	}
}
